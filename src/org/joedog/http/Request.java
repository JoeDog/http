package org.joedog.http;

import java.net.URL;
import java.lang.StringBuilder;

public class Request <K, V> extends Headers {
  public static final String HOST                = "Host";
  public static final String AUTHORIZATION       = "Authorization";
  public static final String COOKIE              = "Cookie";
  public static final String FROM                = "From";
  public static final String IF_MODIFIED_SINCE   = "If-Modified-Since";
  public static final String REFERER             = "Referer";
  public static final String USER_AGENT          = "User-Agent"; 
  
  public static final String ACCEPT              = "Accept"; 
  public static final String ACCEPT_CHARSET      = "Accept-Charset"; 
  public static final String ACCEPT_ENCODING     = "Accept-Encoding"; 
  public static final String ACCEPT_LANGUAGE     = "Accept-Language"; 
  public static final String CONNECTION          = "Connection";
  public static final String EXPECT              = "Expect"; 
  public static final String IF_MATCH            = "If-Match"; 
  public static final String IF_NONE_MATCH       = "If-None-Match"; 
  public static final String IF_RANGE            = "If-Range"; 
  public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since"; 
  public static final String MAX_FORWARDS        = "Max-Forwards"; 
  public static final String PROXY_AUTHORIZATION = "Proxy-Authorization"; 
  public static final String RANGE               = "Range"; 
  public static final String TE                  = "TE"; 
  
  protected double    version;
  protected URL       url;
  protected String    headers[];
  protected String    request; 
  protected Config    conf;
  
  public Request() {
    this.conf = Config.getInstance();
  }

  public void setReferer(String referer) {
    this.put(REFERER, referer); 
  }

  public void setAuthorizationHeader(Authorization.TYPE type, String realm) {
    String header = this.conf.getAuthorizationHeader(type, realm);
    if (header != null) {
      this.put(AUTHORIZATION, header); 
    } 
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.request);
    for (String key : this.keySet()) {
      String line = String.format("%s: %s\015\012", key, this.get(key));
      sb.append(line);
    }
    /** 
     * Each line above contains a carriage return and line feed. 
     * We just another one to complete the request.
     */
    sb.append("\015\012"); 
    return sb.toString();
  }

  protected void build() {
    int  port    = (this.url.getPort() > 0) ? this.url.getPort() : this.url.getDefaultPort();
    this.request = String.format(
      "GET %s HTTP/%s\015\012%s: %s:%d\015\012", 
      this.url.getFile(), String.valueOf(this.version), HOST, this.url.getHost(), port
    );
    for (int i = 0; i < this.headers.length; i++) {
      /************** v1.0 **************/
      if (this.headers[i].equals(HOST)) {
        ; // we put the Host field into our request above 
      }
      if (this.headers[i].equals(AUTHORIZATION)) {
        if (this.conf.getProperty("authorization") != null) {
          this.put(USER_AGENT, this.conf.getProperty("authorization")); 
        } // There is not default value
      }
      if (this.headers[i].equals(FROM)) {
        if (this.conf.getProperty("from") != null) {
          this.put(FROM, this.conf.getProperty("from")); 
        } 
      }
      if (this.headers[i].equals(REFERER)) {
        // XXX: Since the referer is dynamic we set it with a setter 
      }
      if (this.headers[i].equals(USER_AGENT)) {
        if (this.conf.getProperty("user-agent") != null) {
          this.put(USER_AGENT, this.conf.getProperty("user-agent")); 
        } else {  
          this.put(USER_AGENT, String.format("JoeDog/%s", Version.version)); 
        }
      }
      /************** v1.1 **************/
      if (this.headers[i].equals(CONNECTION)) {
        if (this.conf.getProperty("connection") != null) {
          this.put(CONNECTION, this.conf.getProperty("connection")); 
        } else {
          this.put(CONNECTION, "close"); 
        }
      }
    }
  }
}
