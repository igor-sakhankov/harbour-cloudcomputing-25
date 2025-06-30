import boto3

REGION = 'us-east-1'
QUEUE_NAME = 'shift-events'

def main():
    sqs = boto3.client('sqs', region_name=REGION)
    response = sqs.create_queue(QueueName=QUEUE_NAME)
    print(f"Queue URL: {response['QueueUrl']}")

if __name__ == '__main__':
    main()
