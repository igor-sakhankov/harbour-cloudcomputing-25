import boto3
import time
from logger import log_event

ec2 = boto3.resource('ec2', region_name='us-east-1')

with open('user_data.sh', 'r') as file:
    user_data_script = file.read()

start_time = time.time()

instance = ec2.create_instances(
    ImageId='ami-09e6f87a47903347c',  
    MinCount=1,
    MaxCount=1,
    InstanceType='t2.micro',
    KeyName='your-key-name',  
    UserData=user_data_script,
    NetworkInterfaces=[{
        'DeviceIndex': 0,
        'SubnetId': 'subnet-0d6b0743324bc0caa',
        'AssociatePublicIpAddress': True,
        'Groups': ['sg-0ab39d5d99c3c1ebf']  
    }],
    TagSpecifications=[{
        'ResourceType': 'instance',
        'Tags': [{'Key': 'Name', 'Value': 'assignment-2-instance'}]
    }]
)[0]


print("Launching EC2 instance...")
instance.wait_until_running()
instance.reload()
elapsed_time = time.time() - start_time

print(f"Instance is running. Public DNS: {instance.public_dns_name}")
print(f"Startup time: {elapsed_time:.2f} seconds")

log_event("CREATE_INSTANCE", instance.id, f"Startup time: {elapsed_time:.2f}s, PublicDNS: {instance.public_dns_name}")
