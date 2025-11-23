#!/bin/bash

# Traffic Simulator Run Script
# This script compiles and runs the Java traffic simulator

echo "Building Traffic Simulator..."

# Navigate to src directory
cd src

# Compile Java files
javac -d ../build *.java

# Check if compilation was successful
if [ $? -eq 0 ]; then
    echo "Build successful! Running simulator..."
    # Run the application
    java -cp ../build Main
else
    echo "Build failed. Please check for errors."
    exit 1
fi