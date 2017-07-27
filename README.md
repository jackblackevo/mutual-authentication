# mutual-authentication
Two-way SSL authentication Server & Client.

## Server
### Certificate Files
Copy your certificate files (include KeyStore, TrustStore) to: server/src/main/resources

### web.xml
* Path: server/src/main/webapp/WEB-INF/web.xml
* Edit: `keystoreFile`, `keystorePass`, `truststoreFile`

## Client
### Certificate Files
Copy your certificate files (include KeyStore, TrustStore) to: client/src/main/resources

### config.properties
* Path: client/src/main/resources/config.properties
* Edit: `keyStore`, `keyStorePassword`, `trustStore`, `targetURL`
