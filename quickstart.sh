#!/bin/bash

# 🎵 Hare Krishna Music App - Quick Start Script
# This script helps you get started with the project

echo "🎵 Hare Krishna Music Streaming App - Quick Start"
echo "=================================================="
echo ""

# Check if in correct directory
if [ ! -f "build.gradle.kts" ]; then
    echo "❌ Error: Please run this script from the project root directory"
    exit 1
fi

# Step 1: Check for local.properties
if [ ! -f "local.properties" ]; then
    echo "📋 Step 1: Creating local.properties from template..."
    if [ -f "local.properties.example" ]; then
        cp local.properties.example local.properties
        echo "✅ Created local.properties"
        echo "⚠️  Please edit local.properties and add your API keys"
    else
        echo "❌ local.properties.example not found"
        exit 1
    fi
else
    echo "✅ Step 1: local.properties already exists"
fi

echo ""

# Step 2: Check Gradle
echo "📋 Step 2: Checking Gradle..."
if command -v ./gradlew &> /dev/null; then
    echo "✅ Gradle wrapper found"
else
    echo "⚠️  Gradle wrapper not found. Trying ./gradlew..."
fi

echo ""

# Step 3: Sync Gradle
echo "📋 Step 3: Syncing Gradle dependencies..."
echo "This may take a few minutes on first run..."
./gradlew --refresh-dependencies 2>&1 | grep -E "BUILD|SUCCESS|FAILED" || true

echo ""

# Step 4: Build
echo "📋 Step 4: Building the project..."
./gradlew clean build 2>&1 | tail -5

echo ""
echo "=================================================="
echo "✅ Setup Complete!"
echo ""
echo "📚 Next Steps:"
echo "1. Read README.md for full documentation"
echo "2. Edit local.properties with your API credentials"
echo "3. Configure API endpoints in MusicStreamingApp.kt"
echo "4. Run: ./gradlew installDebug"
echo "5. Test on Android device/emulator"
echo ""
echo "📖 Documentation Files:"
echo "  - README.md (Start here)"
echo "  - INTEGRATION_GUIDE.md (API setup)"
echo "  - QUICK_REFERENCE.md (Commands)"
echo "  - PROJECT_SUMMARY.md (Overview)"
echo ""
echo "🚀 Happy Coding!"
echo "=================================================="
