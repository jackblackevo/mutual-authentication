# mutual-authentication
Two-way SSL authentication Server & Client.

## Server
### Certificate Files
Copy your certificate files (include KeyStore, TrustStore) to: server/src/main/resources

### pom.xml
* Path: server/pom.xml
* Edit:
  * `jetty.sslContext.keyStorePath`
  * `jetty.sslContext.keyStorePassword`
  * `jetty.sslContext.keyManagerPassword`
  * `jetty.sslContext.trustStorePath`
  * `jetty.sslContext.trustStorePassword`

### Run & Stop
Run:

```bash
$ mvn jetty:run
```

Stop:

```bash
$ mvn jetty:stop
```

## Client
### Certificate Files
Copy your certificate files (include KeyStore, TrustStore) to: client/src/main/resources

### config.properties
* Path: client/src/main/resources/config.properties
* Edit: `keyStore`, `keyStorePassword`, `trustStore`, `targetURL`
