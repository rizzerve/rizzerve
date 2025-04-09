#!/bin/bash

# Create deploy package for AWS Elastic Beanstalk
# This script zips the necessary files for deployment

echo "Creating deployment package for AWS Elastic Beanstalk..."

# Define the output zip file name
ZIP_FILE="rizzerve-eb-deploy.zip"

# Clean up any existing zip file
if [ -f "$ZIP_FILE" ]; then
    echo "Removing existing $ZIP_FILE..."
    rm "$ZIP_FILE"
fi

# Create zip file with necessary files
echo "Zipping deployment files..."
zip -r "$ZIP_FILE" \
    Dockerfile \
    Dockerrun.aws.json \
    .elasticbeanstalk/ \
    target/rizzerve-0.0.1-SNAPSHOT.jar \
    -x "*.git*" "*/.DS_Store" "*/Thumbs.db"

# Check if the zip was created successfully
if [ -f "$ZIP_FILE" ]; then
    echo "Deployment package created successfully: $ZIP_FILE"
    echo "Size: $(du -h "$ZIP_FILE" | cut -f1)"
    echo "You can now deploy this file to Elastic Beanstalk using the AWS console or eb deploy command."
else
    echo "Failed to create deployment package."
    exit 1
fi
