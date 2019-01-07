package org.joedog.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.nio.file.Path; 
import java.nio.file.Paths;
import java.util.Properties;

public class HTTP {
  private URL        url     = null;
  private boolean    secure  = false;
  private Connection conn    = null;
  private Response   res     = null;
  private Request    req     = null;
  private double     version = 1.1;
  private Config     conf    = null;

  public HTTP() {
    this.conf    = load(null);
    if (this.conf.getProperty("version") != null) {
      this.version = Double.parseDouble(this.conf.getProperty("version"));
    }
  }

  public HTTP(double version) {
    this.version = version;
    this.conf    = load(null);
  }

  public void get(String url) {
    try {
      this.get(new URL(url));
    } catch (MalformedURLException me) {
      System.err.println("ERROR: Malformed URL");
    }
  }

  public void get(URL url) {
    this.url = url;

    if (this.url.getProtocol().equals("https")) {
      this.secure = true;
    }
    this.conn = new Connection(this.url.getHost(), this.url.getDefaultPort(), this.secure);
    RequestFactory factory = new RequestFactoryImpl();
    Request        request = factory.getRequest(url, this.version);
    request.setReferer(url.toString());
    System.out.print(request.toString());
    this.conn.write(request.toString());
    ResponseFactory industry = new ResponseFactoryImpl();
    Response        response = industry.getResponse(this.version);
    while (true) {
      String line = this.conn.readline(); 
      if (line == null) break;
      response.add(line);
    }
    System.out.println(response.toString());
    System.out.println(this.conn.read(8084));

    switch (response.getCode()) {
      case 401: // Authenticate
        request.setAuthorizationHeader(response.getAuthorizationType(), response.getAuthorizationRealm());
        this.conn = new Connection(this.url.getHost(), this.url.getDefaultPort(), this.secure);
        this.conn.write(request.toString());
        System.out.println(request.toString());
        response = industry.getResponse(this.version);
        while (true) {
          String line = this.conn.readline();
          if (line == null) break;
            response.add(line);
        }
        System.out.println(response.toString());
        System.out.println(this.conn.read(8084));

      default:
        return;
    }
  }

  public boolean exists(String name) {
    File file;
    if (name == null) {
      return false;
    }
    file = new File(name);
    return file.exists();
  }

  private Config load(String file) {
    String name = null;
    Config conf = Config.getInstance();
    if (file == null) {
      Path   rel  = Paths.get("");
      String cwd  = rel.toAbsolutePath().toString();
      name = String.format("%s/http.properties", cwd);
    } else {
      name = file;
    }
    if (this.exists(name)) {
      try {
        conf.load(new FileInputStream(name)); 
      } catch (IOException ignore) {}
    }
    return conf;
  }
}

