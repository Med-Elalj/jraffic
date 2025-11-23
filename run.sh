#!/bin/bash
echo "Building Traffic Simulator..."
cd src
javac -d ../build *.java
if [ $? -eq 0 ]; then
    echo "Build successful! Running simulator..."
    java -cp ../build Main
else
    echo "Build failed. Please check for errors."
    exit 1
fi