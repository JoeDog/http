package org.joedog.http;

public class Cookie {
  private String     name     = "";
  private String     value    = "";
  private String     domain   = "";
  private String     path     = "";
  private String     comment  = "";
  private String     expires  = "";
  private Boolean    session  = true;
  private Boolean    secure   = false;
  private Boolean    httpOnly = true;
  private String     none     = "none";

  public Cookie() {
    
  }

  public Cookie(String name, String value) {
    this.name  = name;
    this.value = value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return (this.name != null) ? this.name : this.none;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return (this.value != null) ? this.value : this.none;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getDomain() {
    return (this.domain != null) ? this.domain : this.none;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return (this.path != null) ? this.path : this.none;
  }

  public void setExpires(String expires) {
    this.expires = expires;
  }

  public Boolean isSession() {
    return this.session;
  }

  public Boolean isSecure() {
    return this.secure;
  }

  public void setHttpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
  }

  public boolean isHttpOnly() {
    return this.httpOnly;
  }

  public String toString() {
    return String.format("%s='%s'; domain='%s'; path='%s'; expires='%s' HttpOnly='%s'", this.name, this.value, this.domain, this.path, this.expires, this.httpOnly);
  }
}
