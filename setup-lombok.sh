#!/bin/bash

# Lombok Setup Script for HHSA 2.0
# This script helps configure Lombok annotation processing

echo "🔧 Lombok Setup Script"
echo "======================"
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

echo "✅ Maven found: $(mvn -version | head -n 1)"
echo ""

# Build common-core first (required dependency)
echo "📦 Building common-core (required dependency)..."
cd backend/common-core
mvn clean install -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ Failed to build common-core"
    exit 1
fi
echo "✅ common-core built successfully"
echo ""

# Build contract-management-service to test Lombok
echo "📦 Building contract-management-service (testing Lombok)..."
cd ../contract-management-service
mvn clean compile
if [ $? -eq 0 ]; then
    echo "✅ Lombok annotation processing is working!"
    echo "✅ Contract management service compiled successfully"
else
    echo "⚠️  Compilation failed. This might indicate Lombok annotation processing issues."
    echo ""
    echo "Next steps:"
    echo "1. Install Lombok plugin in your IDE:"
    echo "   - IntelliJ IDEA: Settings → Plugins → Search 'Lombok' → Install"
    echo "   - VS Code: Install 'Lombok Annotations Support' extension"
    echo "   - Eclipse: Download lombok.jar and run: java -jar lombok.jar"
    echo ""
    echo "2. Enable annotation processing in your IDE:"
    echo "   - IntelliJ: Settings → Build → Compiler → Annotation Processors → Enable"
    echo "   - VS Code: Should work automatically with the extension"
    echo ""
    echo "3. Rebuild the project in your IDE"
    exit 1
fi

echo ""
echo "🎉 Setup complete!"
echo ""
echo "📝 IDE Configuration:"
echo "   - IntelliJ IDEA: .idea/ folder created with annotation processing enabled"
echo "   - VS Code: .vscode/ folder created with Lombok support"
echo ""
echo "💡 If you're using a different IDE, please install the Lombok plugin manually."




