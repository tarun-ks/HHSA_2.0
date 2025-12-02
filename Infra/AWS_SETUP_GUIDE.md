# AWS Setup Guide for Azure AD SSO

Your organization uses Azure AD for AWS authentication. Here's how to set it up:

## Quick Setup

```bash
cd Infra/scripts
./configure-aws-azure.sh
```

This will open your browser and guide you through the process.

## Manual Setup (Detailed)

### Step 1: Access AWS via Azure Portal

1. Open your browser and go to:
   ```
   https://myapps.microsoft.com/signin/113614_574894348494_ACP_AWS_APP/b89459e5-03d1-4c8b-931b-4d9b3bb6e0b3?tenantId=e0793d39-0939-496d-b129-198edd916feb
   ```

2. Sign in with your Microsoft credentials

3. Click on the AWS application

### Step 2: Get AWS Credentials

After clicking the AWS app, you'll see your AWS accounts. For each account:

1. Click on **"Command line or programmatic access"**
2. You'll see three options for credentials

### Step 3: Choose Your Method

#### Option A: Environment Variables (Quick, Temporary)

Copy the export commands shown in the portal and paste them in your terminal:

```bash
export AWS_ACCESS_KEY_ID=ASIA...
export AWS_SECRET_ACCESS_KEY=...
export AWS_SESSION_TOKEN=...
```

**Pros**: Quick and easy
**Cons**: Expires after a few hours, need to re-authenticate

#### Option B: AWS CLI Configure (Persistent)

1. Copy your Access Key ID and Secret Access Key from the portal
2. Run:
   ```bash
   aws configure
   ```
3. Paste the credentials when prompted
4. Set region: `us-east-1`
5. Set output: `json`

**Note**: If using temporary credentials, you'll also need to set the session token:
```bash
aws configure set aws_session_token YOUR_SESSION_TOKEN
```

#### Option C: Credentials File (Manual)

1. Create/edit the credentials file:
   ```bash
   mkdir -p ~/.aws
   nano ~/.aws/credentials
   ```

2. Add your credentials:
   ```ini
   [default]
   aws_access_key_id = YOUR_ACCESS_KEY_ID
   aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
   aws_session_token = YOUR_SESSION_TOKEN
   ```

3. Create config file:
   ```bash
   nano ~/.aws/config
   ```

4. Add configuration:
   ```ini
   [default]
   region = us-east-1
   output = json
   ```

### Step 4: Verify Configuration

```bash
aws sts get-caller-identity
```

You should see your account information.

## Troubleshooting

### "Unable to locate credentials"

Your credentials aren't set. Go back to Step 2 and copy the credentials from the portal.

### "The security token included in the request is expired"

Your session has expired. Go back to Step 1 and get new credentials.

### "An error occurred (InvalidRequestException)"

This means AWS SSO isn't configured for your organization. Use the manual credential method instead.

## Session Management

Azure AD temporary credentials typically expire after **1-12 hours**. When they expire:

1. Go back to your Azure portal
2. Get new credentials
3. Update your environment variables or credentials file

## Automation Tip

Create a script to quickly refresh credentials:

```bash
# Save as ~/refresh-aws.sh
#!/bin/bash
echo "Opening AWS portal..."
open "https://myapps.microsoft.com/signin/113614_574894348494_ACP_AWS_APP/b89459e5-03d1-4c8b-931b-4d9b3bb6e0b3?tenantId=e0793d39-0939-496d-b129-198edd916feb"
echo "Copy the export commands and paste them here:"
```

## Next Steps

Once AWS CLI is configured and working:

```bash
# Verify it works
aws sts get-caller-identity

# Deploy infrastructure
cd Infra/scripts
./setup.sh
```

## Need Help?

If you're still having issues:

1. Check with your AWS administrator about your access level
2. Verify you have permissions to create EC2 instances, VPCs, etc.
3. Try using the AWS Console first to verify your access
