@description('Azure region')
param location string

@description('Environment name used for naming')
param environmentName string

@description('ACR login server URL')
param acrLoginServer string

@description('Container image tag')
param imageTag string

@description('Managed identity resource ID')
param identityId string

@description('Cosmos DB endpoint')
@secure()
param cosmosEndpoint string

@description('Cosmos DB key')
@secure()
param cosmosKey string

@description('Cosmos DB database name')
param cosmosDatabase string = 'mmdev2932'

@description('CORS allowed origins (Static Web App hostname)')
param corsAllowedOrigins string

var envName = 'cae-buzzball-${environmentName}'
var appName = 'ca-buzzball-api-${environmentName}'

resource containerAppEnv 'Microsoft.App/managedEnvironments@2024-03-01' = {
  name: envName
  location: location
  properties: {
    zoneRedundant: false
  }
}

resource containerApp 'Microsoft.App/containerApps@2024-03-01' = {
  name: appName
  location: location
  identity: {
    type: 'UserAssigned'
    userAssignedIdentities: {
      '${identityId}': {}
    }
  }
  properties: {
    managedEnvironmentId: containerAppEnv.id
    configuration: {
      activeRevisionsMode: 'Single'
      ingress: {
        external: true
        targetPort: 8080
        transport: 'http'
        allowInsecure: false
      }
      registries: [
        {
          server: acrLoginServer
          identity: identityId
        }
      ]
      secrets: [
        {
          name: 'cosmos-endpoint'
          value: cosmosEndpoint
        }
        {
          name: 'cosmos-key'
          value: cosmosKey
        }
      ]
    }
    template: {
      containers: [
        {
          name: 'buzzball-api'
          image: '${acrLoginServer}/buzzball-api:${imageTag}'
          resources: {
            cpu: json('0.5')
            memory: '1Gi'
          }
          env: [
            {
              name: 'COSMOS_ENDPOINT'
              secretRef: 'cosmos-endpoint'
            }
            {
              name: 'COSMOS_KEY'
              secretRef: 'cosmos-key'
            }
            {
              name: 'COSMOS_DATABASE'
              value: cosmosDatabase
            }
            {
              name: 'SPRING_PROFILES_ACTIVE'
              value: 'azure'
            }
            {
              name: 'APP_CORS_ALLOWED_ORIGINS'
              value: corsAllowedOrigins
            }
          ]
        }
      ]
      scale: {
        minReplicas: 0
        maxReplicas: 2
        rules: [
          {
            name: 'http-scaling'
            http: {
              metadata: {
                concurrentRequests: '10'
              }
            }
          }
        ]
      }
    }
  }
}

@description('Container App FQDN')
output fqdn string = containerApp.properties.configuration.ingress.fqdn

@description('Container App URL')
output url string = 'https://${containerApp.properties.configuration.ingress.fqdn}'
