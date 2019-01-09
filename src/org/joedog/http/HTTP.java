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
  private Connection conn    = null;
  private Response   res     = null;
  private Request    req     = null;
  private double     version = 1.1;
  private Config     conf    = null;

  public HTTP() {
    this.conf    = load(null);
    this.conn    = new Connection();
    if (this.conf.getProperty("version") != null) {
      this.version = Double.parseDouble(this.conf.getProperty("version"));
    }
  }

  public HTTP(double version) {
    this.version = version;
    this.conf    = load(null);
    this.conn    = new Connection();
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
    if (this.conn.isClosed()) {
      this.conn.open(this.url);
    }
    HeaderFactory factory  = new HeaderFactoryImpl();
    Request       request  = factory.getRequest(url, this.version);
    Response      response = factory.getResponse(this.version);
    request.setReferer(url.toString());
    System.out.print(request.toString());
    this.conn.write(request.toString());
    while (true) {
      String line = this.conn.readline(); 
      if (line == null) break;
      response.add(line);
    }
    System.out.println(response.toString());

    System.out.println(this.download(response));

    if (response.getConnectionType() == Connection.TYPE.CLOSE) {
      this.conn.close();
    }

    switch (response.getCode()) {
      case 401: // Authenticate
        request.setAuthorizationHeader(response.getAuthorizationType(), response.getAuthorizationRealm());
        this.conn.open(this.url);
        this.conn.write(request.toString());
        System.out.println(request.toString());
        response = factory.getResponse(this.version);
        while (true) {
          String line = this.conn.readline();
          if (line == null) break;
            response.add(line);
        }
        System.out.println(this.download(response));

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

  /**
   * XXX: Currently reads a string; need to do binary, too
   */
  private String download(Response response) {
    String pg = "";
    TransferEncoding te = new TransferEncoding(response);
    while (te.hasMore()) {
      int len    = te.length(this.conn);
      String tmp = this.conn.read(len);
      if (tmp == null) { 
        te.noMore();
        return pg;
      } else {
        pg += tmp;
      }
    }
    return pg;
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

