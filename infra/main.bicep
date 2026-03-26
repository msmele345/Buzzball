@description('Environment name (dev or prod)')
@allowed(['dev', 'prod'])
param environmentName string

@description('Azure region for all resources')
param location string = resourceGroup().location

@description('Container image tag (e.g., git SHA)')
param backendImageTag string

@description('Cosmos DB endpoint URL')
@secure()
param cosmosEndpoint string

@description('Cosmos DB access key')
@secure()
param cosmosKey string

@description('Cosmos DB database name')
param cosmosDatabase string = 'mmdev2932'

@description('Existing Key Vault name for managed identity access')
param keyVaultName string = 'kvdevapps1'

// --- Modules ---

module acr 'modules/acr.bicep' = {
  name: 'acr-${environmentName}'
  params: {
    location: location
    environmentName: environmentName
  }
}

module staticWebApp 'modules/static-web-app.bicep' = {
  name: 'swa-${environmentName}'
  params: {
    location: location
    environmentName: environmentName
  }
}

module identity 'modules/identity.bicep' = {
  name: 'identity-${environmentName}'
  params: {
    location: location
    environmentName: environmentName
    acrId: acr.outputs.id
    keyVaultName: keyVaultName
  }
}

module containerApp 'modules/container-app.bicep' = {
  name: 'container-app-${environmentName}'
  params: {
    location: location
    environmentName: environmentName
    acrLoginServer: acr.outputs.loginServer
    imageTag: backendImageTag
    identityId: identity.outputs.id
    cosmosEndpoint: cosmosEndpoint
    cosmosKey: cosmosKey
    cosmosDatabase: cosmosDatabase
    corsAllowedOrigins: 'https://${staticWebApp.outputs.defaultHostname}'
  }
}

// --- Outputs ---

@description('ACR login server')
output acrLoginServer string = acr.outputs.loginServer

@description('Container App URL')
output backendUrl string = containerApp.outputs.url

@description('Static Web App hostname')
output frontendHostname string = staticWebApp.outputs.defaultHostname

@description('Static Web App resource name')
output swaName string = staticWebApp.outputs.name
