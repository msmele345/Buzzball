@description('Azure region for the Static Web App')
param location string

@description('Environment name used for naming')
param environmentName string

var swaName = 'swa-buzzball-${environmentName}'

resource staticWebApp 'Microsoft.Web/staticSites@2023-12-01' = {
  name: swaName
  location: location
  sku: {
    name: 'Free'
    tier: 'Free'
  }
  properties: {}
}

@description('Static Web App default hostname')
output defaultHostname string = staticWebApp.properties.defaultHostname

@description('Static Web App resource name')
output name string = staticWebApp.name
