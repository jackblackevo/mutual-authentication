package idv.jackblackevo;

import javax.json.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.X509Certificate;

public class MutualAuthenticationServlet extends HttpServlet {
  // Tomcat server.xml settings:
  // <Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
  //            maxThreads="150" SSLEnabled="true"
  //            scheme="https" secure="true" clientAuth="true" sslProtocol="TLS"
  //            keystoreFile="path/to/keyStoreFile" keystorePass="password"
  //            truststoreFile="path/to/trustStoreFile" />
  // clientAuth="true" 表示開啟雙向 SSL

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    JsonObjectBuilder responseJOB = Json.createObjectBuilder();

    X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
    // Jetty 的 sslContextFactory 設定為 needClientAuth="false" 的話，certs 會是 null
    if (certs != null) {
      JsonArrayBuilder jArrayBuilder = Json.createArrayBuilder();
      for (int i = 0; i < certs.length; i++) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        jsonBuilder.add("isVerify", verifyCertificate(certs[i]));
        jsonBuilder.add("detail", certs[i].toString());

        jArrayBuilder.add(jsonBuilder);
      }

      responseJOB.add("clientCertificates", jArrayBuilder);
    } else {
      if ("https".equalsIgnoreCase(request.getScheme())) {
        responseJOB.add("errorMessage", "HTTPS Request: client certificate not found");
      } else {
        responseJOB.add("errorMessage", "HTTP: can not get client certificate");
      }
    }

    JsonObject responseJson = responseJOB.build();
    try (
      PrintWriter out = response.getWriter()
    ) {
      out.println(responseJson);
      out.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    doGet(request, response);
  }

  // Check Client Certificate
  private boolean verifyCertificate(X509Certificate certificate) {
    boolean valid = false;
    try {
      certificate.checkValidity();
      valid = true;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return valid;
  }
}
