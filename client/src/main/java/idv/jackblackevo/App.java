package idv.jackblackevo;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.net.ssl.*;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Properties;

public class App {
  private static final Properties props = new Properties();

  static {
    try {
      props.load(App.class.getClassLoader().getResourceAsStream("config.properties"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static final String KEYSTORE_PATH = App.class.getClassLoader().getResource(props.getProperty("keyStore")).getPath();
  private static final String KEYSTORE_PASSWORD = props.getProperty("keyStorePassword");
  private static final String TRUSTSTORE_PATH = App.class.getClassLoader().getResource(props.getProperty("trustStore")).getPath();
  private static final String TARGET_URL = props.getProperty("targetURL");

  public static void main(String[] args) {
    // 若設定以下系統參數，也可以不用自行設定 HttpsURLConnection 的 SSLSocketFactory
    // 執行時會自動以系統參數所設定的 Client KeyStore、TrustStore 之 defaultSSLSocketFactory 來進行連線
    // 但此方式僅能設定一組 Client KeyStore、TrustStore
//    System.setProperty("javax.net.ssl.keyStore", KEYSTORE_PATH);
//    System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);
//    System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_PATH);

    if (Boolean.parseBoolean(props.getProperty("SSLDebugMode"))) {
      System.setProperty("javax.net.debug", "ssl");
    }

    JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
    jsonBuilder.add("msg", "Hello World!");
    JsonObject data = jsonBuilder.build();

    JsonObject receivedData = sendJSON(data, TARGET_URL);

    System.out.println(receivedData);
  }

  private static JsonObject sendJSON(JsonObject data, String targetURL) {
    JsonObject receivedData = null;
    try {
      HttpsURLConnection connection = (HttpsURLConnection) new URL(targetURL).openConnection();
      // 設定 HttpsURLConnection 的 SSLSocketFactory
      connection.setSSLSocketFactory(getSSLSocketFactory());

      connection.setRequestMethod("POST");
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setRequestProperty("Accept", "application/json");

      try (OutputStream output = connection.getOutputStream();
           PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"), true)) {
        writer.write(data.toString());
        output.flush();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        StringBuilder responseBodySB = new StringBuilder();
        String tempStr;
        while ((tempStr = reader.readLine()) != null) {
          responseBodySB.append(tempStr);
        }

        JsonReader jsonReader = Json.createReader(new StringReader(responseBodySB.toString()));
        receivedData = jsonReader.readObject();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return receivedData;
  }

  private static SSLSocketFactory getSSLSocketFactory() {
    SSLSocketFactory sslSocketFactory = null;
    try (
      InputStream keyStoreInputStream = new FileInputStream(KEYSTORE_PATH);
      InputStream trustStoreInputStream = new FileInputStream(TRUSTSTORE_PATH)
    ) {
      // 讀取 Client KeyStore、TrustStore
      KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(keyStoreInputStream, KEYSTORE_PASSWORD.toCharArray());
      keyStoreInputStream.close();

      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

      KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
      trustStore.load(trustStoreInputStream, null);
      trustStoreInputStream.close();

      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(trustStore);

      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

      sslSocketFactory = sslContext.getSocketFactory();
    } catch (KeyStoreException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (CertificateException e) {
      e.printStackTrace();
    } catch (UnrecoverableKeyException e) {
      e.printStackTrace();
    } catch (KeyManagementException e) {
      e.printStackTrace();
    }

    return sslSocketFactory;
  }
}
