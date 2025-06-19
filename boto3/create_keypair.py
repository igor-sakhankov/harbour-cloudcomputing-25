import boto3

KEY_NAME = 'my-new-keypair'  # Choose a unique name for your keypair
KEY_FILE = KEY_NAME + '.pem'
REGION = 'us-east-1'         # Change if using a different AWS region

def main():
    ec2 = boto3.client('ec2', region_name=REGION)

    print(f"Creating key pair '{KEY_NAME}'...")
    response = ec2.create_key_pair(KeyName=KEY_NAME)
    private_key = response['KeyMaterial']

    with open(KEY_FILE, 'w') as file:
        file.write(private_key)
    print(f"Private key saved to: {KEY_FILE}")

    # Set permissions so only you can read it (important for SSH)
    import os
    os.chmod(KEY_FILE, 0o400)
    print(f"Permissions set to 400 (owner read-only) for {KEY_FILE}")

    print(f"Key pair '{KEY_NAME}' created and saved.")

if __name__ == '__main__':
    main()
