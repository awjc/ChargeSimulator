#!/bin/bash

# ChargeSimulator JAR Build Script
# Works on macOS and WSL

set -e  # Exit on error

echo "Building ChargeSimulator.jar..."

# Create build directory
BUILD_DIR="build"
rm -rf "$BUILD_DIR"
mkdir -p "$BUILD_DIR"

# Compile Java sources
echo "Compiling Java sources..."
find src -name "*.java" -type f > sources.txt
javac -d "$BUILD_DIR" -encoding UTF-8 @sources.txt
rm sources.txt

# Copy resources
echo "Copying resources..."
cp -r src/resources "$BUILD_DIR/"

# Create JAR with manifest
echo "Creating JAR file..."
jar cfm ChargeSimulator.jar META-INF/MANIFEST.MF -C "$BUILD_DIR" .

# Clean up build directory
rm -rf "$BUILD_DIR"

echo "Build complete! ChargeSimulator.jar created successfully. Running..."

./run.sh
