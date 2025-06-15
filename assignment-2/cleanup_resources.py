import boto3
from logger import log_event

ec2 = boto3.client('ec2', region_name='eu-west-3')

instance_ids = ['your-instance-id-1', 'your-instance-id-2']
image_id = 'your-ami-id'
snapshot_ids = ['your-snapshot-id']

ec2.terminate_instances(InstanceIds=instance_ids)
print("Terminating instances...")
for i in instance_ids:
    log_event("TERMINATE_INSTANCE", i)

ec2.deregister_image(ImageId=image_id)
print("AMI deregistered.")
log_event("DEREGISTER_AMI", image_id)

for sid in snapshot_ids:
    ec2.delete_snapshot(SnapshotId=sid)
    print(f"Deleted snapshot: {sid}")
    log_event("DELETE_SNAPSHOT", sid)
