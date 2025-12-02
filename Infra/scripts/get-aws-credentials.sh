#!/bin/bash

echo "=== AWS Credentials Setup Guide ==="
echo ""
echo "After logging into the Microsoft portal, follow these steps:"
echo ""
echo "1. Look for an 'AWS' tile/application in your portal"
echo "2. Click on it - this should redirect you to AWS"
echo "3. You might see multiple options:"
echo "   - 'Management console' - Opens AWS web console"
echo "   - 'Programmatic access' or 'Command line access'"
echo ""
echo "If you see the AWS Console (web interface):"
echo "  a. Look for your username in the top-right corner"
echo "  b. Click on it"
echo "  c. Look for 'Command line or programmatic access'"
echo "  d. Click it to get credentials"
echo ""
echo "If you DON'T see that option, you may need to:"
echo "  - Contact your AWS administrator"
echo "  - Request programmatic access permissions"
echo ""
echo "Alternative: Use AWS Access Portal"
echo "  Some organizations use: https://[your-org].awsapps.com/start"
echo ""
echo "What do you see after clicking the AWS application?"
echo "  A) AWS Console (web interface)"
echo "  B) List of AWS accounts"
echo "  C) Something else"
echo ""
read -p "Enter A, B, or C: " choice

case $choice in
  A|a)
    echo ""
    echo "Great! In the AWS Console:"
    echo "1. Click your username (top-right corner)"
    echo "2. Look for 'Command line or programmatic access'"
    echo "3. Copy the credentials shown"
    ;;
  B|b)
    echo ""
    echo "Perfect! You should see your AWS account(s)."
    echo "Next to each account, look for:"
    echo "  - 'Management console' link"
    echo "  - 'Command line or programmatic access' link"
    echo ""
    echo "Click 'Command line or programmatic access'"
    echo "Then copy the export commands shown"
    ;;
  C|c)
    echo ""
    echo "Please describe what you see, and I'll help you navigate."
    ;;
esac

echo ""
echo "Once you have the credentials, paste them here:"
echo "(They should start with 'export AWS_ACCESS_KEY_ID=...')"
echo ""
