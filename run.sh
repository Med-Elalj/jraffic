#!/bin/bash
run_tests=false

if [[ "$1" == "-t" ]]; then
    echo "Compiling tests..."
    javac -cp lib/*.jar -d build src/*.java tests/*.java
    if [ $? -ne 0 ]; then
        echo -e "Build failed during test compile."
        exit 1
    fi
    echo -e "Running tests..."
    java -cp "lib/*:build" org.junit.platform.console.ConsoleLauncher execute --scan-class-path
    exit $?
    run_tests=true
else 
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
fi