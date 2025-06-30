import boto3
from logger import log_event

ec2 = boto3.client('ec2', region_name='eu-west-3')
instance_id = 'your-instance-id'

response = ec2.create_image(
    InstanceId=instance_id,
    Name='assignment2-ami-snapshot',
    NoReboot=True
)

image_id = response['ImageId']
print(f"AMI created: {image_id}")

waiter = ec2.get_waiter('image_available')
waiter.wait(ImageIds=[image_id])
print("AMI is available.")

log_event("CREATE_AMI", image_id)
