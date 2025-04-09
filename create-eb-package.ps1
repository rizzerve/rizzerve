# PowerShell script to create a deployment package for AWS Elastic Beanstalk with Linux compatibility

Write-Host "Creating deployment package for AWS Elastic Beanstalk..." -ForegroundColor Green

# Define the output zip file name
$ZIP_FILE = "rizzerve-eb-deploy.zip"

# Clean up any existing zip file
if (Test-Path $ZIP_FILE) {
    Write-Host "Removing existing $ZIP_FILE..." -ForegroundColor Yellow
    Remove-Item $ZIP_FILE -Force
}

# Create a temporary directory to organize files with forward slashes for Linux compatibility
$TEMP_DIR = "./eb-deploy-temp"
if (Test-Path $TEMP_DIR) {
    Remove-Item $TEMP_DIR -Recurse -Force
}
New-Item -ItemType Directory -Path $TEMP_DIR | Out-Null

# Create necessary directories with correct structure
New-Item -ItemType Directory -Path "$TEMP_DIR/.elasticbeanstalk" | Out-Null
New-Item -ItemType Directory -Path "$TEMP_DIR/target" | Out-Null

# Copy necessary files to the temp directory, maintaining Linux path structure
Write-Host "Preparing deployment files..." -ForegroundColor Cyan
Copy-Item "Dockerfile" -Destination "$TEMP_DIR/Dockerfile"
Copy-Item "Dockerrun.aws.json" -Destination "$TEMP_DIR/Dockerrun.aws.json"
Copy-Item ".elasticbeanstalk/config.yml" -Destination "$TEMP_DIR/.elasticbeanstalk/config.yml"
Copy-Item ".elasticbeanstalk/optionsettings.config" -Destination "$TEMP_DIR/.elasticbeanstalk/optionsettings.config"
Copy-Item "target/rizzerve-0.0.1-SNAPSHOT.jar" -Destination "$TEMP_DIR/target/rizzerve-0.0.1-SNAPSHOT.jar"

# Create a docker-compose.yml file for Elastic Beanstalk that builds locally instead of pulling
$dockerComposeContent = @"
services:
  app:
    build: .
    ports:
      - "80:8080"
    environment:
      - RDS_URL=\${DB_URL}
      - RDS_USERNAME=\${DB_USERNAME}
      - RDS_PASSWORD=\${DB_PASSWORD}
"@

Set-Content -Path "$TEMP_DIR/docker-compose.yml" -Value $dockerComposeContent

# Create zip file using 7zip for proper Linux path handling
Write-Host "Creating zip archive..." -ForegroundColor Cyan

# Try to find 7zip or use native PowerShell compression as fallback
$sevenZipPath = "C:\Program Files\7-Zip\7z.exe"
if (Test-Path $sevenZipPath) {
    # Use 7zip which handles paths better for Linux
    Write-Host "Using 7-Zip for compression (better Linux compatibility)..." -ForegroundColor Cyan
    
    # Change directory to temp folder to ensure relative paths
    $currentDir = Get-Location
    Set-Location $TEMP_DIR
    
    # Create zip with 7-Zip (which preserves forward slashes better)
    & "$sevenZipPath" a -tzip "..\$ZIP_FILE" "." -r
    
    # Return to original directory
    Set-Location $currentDir
} else {
    # Fallback to .NET but with better handling of path separators
    Write-Host "7-Zip not found, using .NET compression (please consider installing 7-Zip for better compatibility)..." -ForegroundColor Yellow
    
    # Create a temp ZIP file
    $tempZip = [System.IO.Path]::GetTempFileName()
    Remove-Item $tempZip -Force
    $tempZip = $tempZip + ".zip"
    
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    [System.IO.Compression.ZipFile]::CreateFromDirectory($TEMP_DIR, $tempZip)
    
    # Copy with new name to ensure it's cleaned up
    Copy-Item $tempZip -Destination $ZIP_FILE -Force
    Remove-Item $tempZip -Force
}

# Cleanup temp directory
Remove-Item $TEMP_DIR -Recurse -Force

# Check if the zip was created successfully
if (Test-Path $ZIP_FILE) {
    $fileSize = (Get-Item $ZIP_FILE).Length / 1MB
    $fileSizeRounded = [math]::Round($fileSize, 2)
    Write-Host "Deployment package created successfully: $ZIP_FILE" -ForegroundColor Green
    Write-Host "Size: $fileSizeRounded MB" -ForegroundColor Green
    Write-Host "You can now deploy this file to Elastic Beanstalk using the AWS console or eb deploy command." -ForegroundColor Green
} else {
    Write-Host "Failed to create deployment package." -ForegroundColor Red
    exit 1
}
