import boto3
import time
import requests
import sys

REGION = 'us-east-1'
AMI_ID = 'ami-0c02fb55956c7d316'         # Amazon Linux 2 AMI in us-east-1
INSTANCE_TYPE = 't2.micro'
KEY_NAME = 'my-new-keypair'              # Replace with your actual key pair name
SECURITY_GROUP_NAME = 'auto-created-sg'
APP_PORT = 8080                          # Default Spring Boot port
HEALTH_PATH = '/health'                  # Health check path for the shiftbooking-server

USER_DATA = '''#!/bin/bash
yum update -y
sudo yum install -y docker

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Pull the pre-built Docker image from Docker Hub
echo "Pulling the shiftbooking-server Docker image from Docker Hub..."
sudo docker pull alashchev17/shiftbooking-server:latest

# Run the Docker container
echo "Running the shiftbooking-server Docker container..."
sudo docker run -d -p 8080:8080 --name shiftbooking-server alashchev17/shiftbooking-server:latest

# Check if container is running
echo "Checking container status..."
docker ps

echo "Shiftbooking server started in Docker container. Use 'sudo docker logs shiftbooking-server' to view logs."
'''

def get_default_vpc_id(ec2_client):
    vpcs = ec2_client.describe_vpcs(Filters=[{'Name': 'isDefault', 'Values': ['true']}])['Vpcs']
    if not vpcs:
        raise Exception('No default VPC found.')
    return vpcs[0]['VpcId']

def create_security_group(ec2_client, vpc_id):
    sg_response = ec2_client.create_security_group(
        GroupName=SECURITY_GROUP_NAME,
        Description='Security group for automated EC2 launch (SSH + app port)',
        VpcId=vpc_id
    )
    sg_id = sg_response['GroupId']
    ec2_client.authorize_security_group_ingress(
        GroupId=sg_id,
        IpPermissions=[
            {'IpProtocol': 'tcp', 'FromPort': 22, 'ToPort': 22, 'IpRanges': [{'CidrIp': '0.0.0.0/0'}]},
            {'IpProtocol': 'tcp', 'FromPort': APP_PORT, 'ToPort': APP_PORT, 'IpRanges': [{'CidrIp': '0.0.0.0/0'}]}
        ]
    )
    return sg_id

def wait_for_health(public_dns, port, path, timeout=600, interval=5):
    url = f"http://{public_dns}:{port}{path}"
    print(f"Waiting for health check at {url} ...")
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            r = requests.get(url, timeout=2)
            if r.ok:
                print(f"Health check succeeded after {int(time.time() - start_time)} seconds.")
                return time.time() - start_time
        except Exception:
            pass
        time.sleep(interval)
    raise Exception("Health check timed out.")

def create_instance(ec2, ec2_client, sg_id, user_data, name_tag='AutomatedInstance'):
    instance = ec2.create_instances(
        ImageId=AMI_ID,
        MinCount=1,
        MaxCount=1,
        InstanceType=INSTANCE_TYPE,
        KeyName=KEY_NAME,
        SecurityGroupIds=[sg_id],
        UserData=user_data,
        TagSpecifications=[
            {'ResourceType': 'instance', 'Tags': [{'Key': 'Name', 'Value': name_tag}]}
        ]
    )[0]
    print(f"Waiting for instance {instance.id} to run...")
    instance.wait_until_running()
    instance.reload()
    public_dns = instance.public_dns_name
    print(f"Instance {instance.id} is running at {public_dns}")
    return instance

def create_ami(ec2_client, instance_id, name='automated-ami'):
    print(f"Creating AMI from instance {instance_id} ...")
    response = ec2_client.create_image(
        InstanceId=instance_id,
        Name=name,
        NoReboot=True
    )
    image_id = response['ImageId']
    waiter = ec2_client.get_waiter('image_available')
    print("Waiting for AMI to become available...")
    waiter.wait(ImageIds=[image_id])
    print(f"AMI {image_id} is available.")
    return image_id

def terminate_instance(instance):
    print(f"Terminating instance {instance.id} ...")
    instance.terminate()
    instance.wait_until_terminated()
    print(f"Instance {instance.id} terminated.")

def delete_ami_and_snapshots(ec2_client, image_id):
    print(f"Deregistering AMI {image_id} ...")
    # Find all snapshots for this AMI and delete them too
    images = ec2_client.describe_images(ImageIds=[image_id])['Images']
    snapshot_ids = []
    for image in images:
        for bd in image.get('BlockDeviceMappings', []):
            if 'Ebs' in bd and 'SnapshotId' in bd['Ebs']:
                snapshot_ids.append(bd['Ebs']['SnapshotId'])
    ec2_client.deregister_image(ImageId=image_id)
    for snap_id in snapshot_ids:
        print(f"Deleting snapshot {snap_id} ...")
        ec2_client.delete_snapshot(SnapshotId=snap_id)

def delete_security_group(ec2_client, sg_id):
    print(f"Deleting security group {sg_id} ...")
    try:
        ec2_client.delete_security_group(GroupId=sg_id)
        print(f"Security group {sg_id} deleted.")
    except Exception as e:
        print(f"Failed to delete security group: {e}")

def main():
    ec2 = boto3.resource('ec2', region_name=REGION)
    ec2_client = boto3.client('ec2', region_name=REGION)
    vpc_id = get_default_vpc_id(ec2_client)
    sg_id = create_security_group(ec2_client, vpc_id)

    # 1. Launch base instance, measure startup time with health check
    print("\nLaunching base EC2 instance...")
    t0 = time.time()
    base_instance = create_instance(ec2, ec2_client, sg_id, USER_DATA, name_tag="BaseInstance")
    health_time1 = wait_for_health(base_instance.public_dns_name, APP_PORT, HEALTH_PATH)
    t1 = time.time()
    print(f"\n[Base EC2] Time until health check OK: {health_time1:.1f} seconds.")
    print(f"[Base EC2] Total time from launch: {t1-t0:.1f} seconds.")

    # 2. Create AMI from base instance
    image_id = create_ami(ec2_client, base_instance.id)

    # 3. Launch new instance from AMI, measure startup time with health check
    print("\nLaunching EC2 instance from AMI...")
    t2 = time.time()
    ami_instance = create_instance(ec2, ec2_client, sg_id, USER_DATA, name_tag="AmiInstance")
    health_time2 = wait_for_health(ami_instance.public_dns_name, APP_PORT, HEALTH_PATH)
    t3 = time.time()
    print(f"\n[AMI EC2] Time until health check OK: {health_time2:.1f} seconds.")
    print(f"[AMI EC2] Total time from launch: {t3-t2:.1f} seconds.")

    # 4. Clean up resources
    print("\nCleaning up resources...")
    terminate_instance(base_instance)
    terminate_instance(ami_instance)
    delete_ami_and_snapshots(ec2_client, image_id)
    delete_security_group(ec2_client, sg_id)

    print("\nAll done!")

if __name__ == '__main__':
    main()
