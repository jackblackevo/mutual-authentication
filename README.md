# mutual-authentication
Two-way SSL authentication Server & Client.

## Server
### Certificate Files
Copy your certificate files (include KeyStore, TrustStore) to: server/src/main/resources

### jetty-ssl.xml
* Path: server/jetty-ssl.xml
* Edit: `KeyStorePath`, `KeyStorePassword`, `TrustStorePath`

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
