# harbour-cloudcomputing-25
harbour-cloudcomputing-25

## AWS SQS queue creation

Use `boto3/create_sqs_queue.py` to create the `shift-events` queue. The script prints the created queue URL which should be provided to the application via the `AWS_SQS_QUEUE_URL` environment variable.
