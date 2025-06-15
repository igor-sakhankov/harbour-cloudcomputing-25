import boto3
import time
from logger import log_event

ec2 = boto3.resource('ec2', region_name='eu-west-3')

ami_id = 'your-ami-id'
start_time = time.time()

instance = ec2.create_instances(
    ImageId=ami_id,
    MinCount=1,
    MaxCount=1,
    InstanceType='t2.micro',
    KeyName='your-key-name',
    SecurityGroupIds=['your-security-group-id']
)[0]

print("Launching instance from AMI...")
instance.wait_until_running()
instance.reload()
elapsed_time = time.time() - start_time

print(f"Instance started from AMI. Public DNS: {instance.public_dns_name}")
print(f"Startup time: {elapsed_time:.2f} seconds")

log_event("LAUNCH_FROM_AMI", instance.id, f"Startup time: {elapsed_time:.2f}s, PublicDNS: {instance.public_dns_name}")
