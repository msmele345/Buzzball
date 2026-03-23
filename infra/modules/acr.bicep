@description('Azure region for the ACR resource')
param location string

@description('Environment name used for naming')
param environmentName string

var acrName = 'buzzballacr${environmentName}'

resource acr 'Microsoft.ContainerRegistry/registries@2023-07-01' = {
  name: acrName
  location: location
  sku: {
    name: 'Basic'
  }
  properties: {
    adminUserEnabled: false
  }
}

@description('ACR login server URL')
output loginServer string = acr.properties.loginServer

@description('ACR resource ID')
output id string = acr.id

@description('ACR name')
output name string = acr.name
