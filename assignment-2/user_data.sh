#!/bin/bash
yum update -y
yum install git java-21-amazon-corretto -y

cd /home/ec2-user
git clone https://github.com/harhota/cloud-systems-client.git
cd cloud-systems-client/client

./gradlew build
java -jar build/libs/client.jar > output.log
