# Infrastructure Setup

One-time Azure and GitHub configuration required before the CI/CD pipeline can run.

## Prerequisites

- Azure CLI (`az`) installed and logged in
- GitHub CLI (`gh`) installed (optional, for setting secrets)
- Owner or Contributor access on the Azure subscription

## 1. Create Resource Groups

```bash
az group create --name rg-buzzball-dev --location eastus
az group create --name rg-buzzball-prod --location eastus
```

## 2. Create Azure AD App Registration (for OIDC)

```bash
# Create the app registration
az ad app create --display-name "buzzball-github-deploy"

# Note the appId from the output — this is your AZURE_CLIENT_ID
APP_ID=$(az ad app list --display-name "buzzball-github-deploy" --query "[0].appId" -o tsv)

# Create a service principal
az ad sp create --id $APP_ID

# Assign Contributor role (scoped to the subscription or resource groups)
SUBSCRIPTION_ID=$(az account show --query id -o tsv)
az role assignment create \
  --assignee $APP_ID \
  --role Contributor \
  --scope /subscriptions/$SUBSCRIPTION_ID
```

## 3. Add Federated Credentials for GitHub

Create one federated credential per GitHub environment:

```bash
REPO="<your-github-org>/buzzball"

# Dev environment
az ad app federated-credential create --id $APP_ID --parameters '{
  "name": "github-dev",
  "issuer": "https://token.actions.githubusercontent.com",
  "subject": "repo:'"$REPO"':environment:dev",
  "audiences": ["api://AzureADTokenExchange"]
}'

# Prod environment
az ad app federated-credential create --id $APP_ID --parameters '{
  "name": "github-prod",
  "issuer": "https://token.actions.githubusercontent.com",
  "subject": "repo:'"$REPO"':environment:prod",
  "audiences": ["api://AzureADTokenExchange"]
}'
```

## 4. Configure GitHub

### Create Environments

In your GitHub repo settings, create two environments:
- **dev** — no protection rules
- **prod** — add yourself as a required reviewer

### Set Repository Secrets

| Secret | Value |
|--------|-------|
| `AZURE_CLIENT_ID` | App registration client ID from step 2 |
| `AZURE_TENANT_ID` | `az account show --query tenantId -o tsv` |
| `AZURE_SUBSCRIPTION_ID` | `az account show --query id -o tsv` |
| `COSMOS_ENDPOINT` | Your Cosmos DB endpoint URL |
| `COSMOS_KEY` | Your Cosmos DB primary key |
| `ACR_NAME` | Will be `buzzballacr<env>` after first Bicep deploy (e.g., `buzzballacrdev`) |

## 5. Bootstrap (First Deploy)

The first deployment must create the ACR before the pipeline can push images.
Run the Bicep deployment manually to bootstrap:

```bash
# Deploy infrastructure (no container image yet — use a placeholder tag)
az deployment group create \
  --resource-group rg-buzzball-dev \
  --template-file infra/main.bicep \
  --parameters \
    environmentName=dev \
    backendImageTag=init \
    cosmosEndpoint='<your-cosmos-endpoint>' \
    cosmosKey='<your-cosmos-key>'
```

After this, the ACR exists. Set the `ACR_NAME` secret in GitHub, and subsequent pushes to `main` will use the full pipeline.

## Validate Bicep (without deploying)

```bash
az deployment group validate \
  --resource-group rg-buzzball-dev \
  --template-file infra/main.bicep \
  --parameters \
    environmentName=dev \
    backendImageTag=test \
    cosmosEndpoint='https://placeholder' \
    cosmosKey='placeholder'
```
