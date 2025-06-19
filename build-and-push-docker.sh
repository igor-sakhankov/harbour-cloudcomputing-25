#!/bin/bash
# Script to build and push the shiftbooking-server Docker image to Docker Hub
# Run this script locally, not on the EC2 instance

set -e

# Change these variables
DOCKER_USERNAME="alashchev17"  # Replace with your Docker Hub username
IMAGE_NAME="shiftbooking-server"
TAG="latest"

# Download and use Gradle directly
echo "Setting up Gradle..."
GRADLE_VERSION="8.14"
GRADLE_DIR="gradle-${GRADLE_VERSION}"
GRADLE_ZIP="${GRADLE_DIR}-bin.zip"

# Check if we already have this version of Gradle
if [ ! -d "$GRADLE_DIR" ]; then
    # Download Gradle
    wget "https://services.gradle.org/distributions/${GRADLE_ZIP}"
    unzip -q "${GRADLE_ZIP}"
    rm "${GRADLE_ZIP}"
fi

# Use the downloaded Gradle
GRADLE_PATH="$(pwd)/${GRADLE_DIR}/bin/gradle"
chmod +x "$GRADLE_PATH"

# Build the application
echo "Building the application with Gradle..."
"$GRADLE_PATH" :shiftbooking-server:build

# Build the Docker image
echo "Building Docker image..."
docker build -t $DOCKER_USERNAME/$IMAGE_NAME:$TAG -f ./shiftbooking-server/Dockerfile ./shiftbooking-server

# Login to Docker Hub (you'll be prompted for password)
echo "Logging in to Docker Hub..."
docker login -u $DOCKER_USERNAME

# Push the image to Docker Hub
echo "Pushing image to Docker Hub..."
docker push $DOCKER_USERNAME/$IMAGE_NAME:$TAG

echo "Image successfully pushed to Docker Hub as $DOCKER_USERNAME/$IMAGE_NAME:$TAG"
