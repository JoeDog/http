package org.joedog.http;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Response <K, V> extends Headers {
  public static final String WWW_AUTHENTICATE   = "WWW-Authenticate";  


  public static final String CONNECTION         = "Connection";
  public static final String TRANSFER_ENCODING  = "Transfer-Encoding";

  protected int                 code      = 0;
  protected Authorization.TYPE type      = Authorization.TYPE.BASIC;
  protected Connection.TYPE     conn      = Connection.TYPE.CLOSE;
  protected String              realm     = null;
  protected double              version   = 1.0;
  protected String              message   = null; 
  protected StringBuilder       res       = null;

  public Response() { 
    this.res = new StringBuilder();
  }

  public void add(String line) {
    this.res.append(line);
    this.res.append("\015\012");
    if (line.startsWith("HTTP/")) {
      this.parseResponse(line);
    }
    if (line.startsWith(WWW_AUTHENTICATE)) {
      this.parseAuthenticate(line);
    }
  }

  public int getCode() {
    return this.code;
  }

  public Authorization.TYPE getAuthorizationType() {
    return this.type;
  }

  public Connection.TYPE getConnectionType() {
    String conn = this.get(CONNECTION);
    if (conn != null) {
      if (conn.toLowerCase().equals("close"))      return Connection.TYPE.CLOSE;
      if (conn.toLowerCase().equals("keep-alive")) return Connection.TYPE.KEEP_ALIVE;
    }
    return Connection.TYPE.CLOSE;
  }

  public String getAuthorizationRealm() {
    return this.realm;
  }

  public String toString() {
    return this.res.toString();
  }

  private void parseResponse(String line) {
    String res[] = null;
    int count    = line.length() - line.replace(" ", "").length();   
    if (count < 2) {
      System.err.println("ERROR: Malformed Response");
      this.code    = 500;
      this.version = 1.1;
      return;
    } 
    res = line.split(" ", 3);
    this.version = Double.parseDouble(res[0].substring(5, res[0].length()));
    this.code    = Integer.parseInt(res[1]);
    this.message = res[2];
  }

  private void parseAuthenticate(String line) {
    //WWW-Authenticate: Basic realm="basic area"    
    String res[] = null;
    if (! line.contains(":")) {
      return; // this is an unexpected turn of events....
    }
    res = line.split(":", 2);
    if (res[1] == null || res[1].length() < 1) {
      return; // WTF??
    }
    res[1].trim();
    if (res[1].toLowerCase().startsWith("basic")) {
      this.type = Authorization.TYPE.BASIC;
    } else if (res[1].toLowerCase().startsWith("digest")) {
      this.type = Authorization.TYPE.BASIC;
    } else if (res[1].toLowerCase().startsWith("ntlm")) {
      this.type = Authorization.TYPE.NTLM;
    } else {
      this.type = Authorization.TYPE.UNSUPPORTED;
    }
    Pattern p = Pattern.compile("realm=\"([^\"]*)\"");
    Matcher m = p.matcher(line);
    while (m.find()) {
      this.realm = m.group(1);
    }    
  }
}

