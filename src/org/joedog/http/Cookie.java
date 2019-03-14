package org.joedog.http;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;

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
  private Date       date     = null;

  private static String RFC_1123 = "EEE, dd-MMM-yyyy HH:mm:ss Z";

  public Cookie() {
    
  }

  public Cookie(String name, String value) {
    this.name  = name;
    this.value = value;
  }

  public Cookie(String name, String value, String path) {
    this(name, value);
    if (path != null && path.length() > 0) this.setPath(path);
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

  public boolean inDomain(String host) {
    if (this.domain == null) {
      return true; // never set a domain  
    }

    boolean b = host.endsWith(this.domain);
    return b;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return (this.path != null) ? this.path : this.none;
  }

  public boolean inPath(String path) {
    if (this.path == null || this.path.length() < 1) { 
      return true; // never set a path
    }

    boolean b = path.startsWith(this.path);
    return b;
  }

  public void setExpires(String expires) {
    if (expires == null || expires.length() < 1) 
      return;

    try {
      SimpleDateFormat sdf  = new SimpleDateFormat(this.RFC_1123);
      Calendar         cal  = Calendar.getInstance();
      cal.setTime(sdf.parse(expires));
      this.date = cal.getTime();
    } catch (ParseException pe) {}
  }

  public boolean isExpired() {
    if (this.date == null) {  
      return false; // never set an expiration
    }

    Date now = new Date();
    if (now.after(this.date)) {
      return true;
    }
    return false;
  }

  public boolean isSession() {
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
    return String.format("%s='%s'; domain='%s'; path='%s'; expires='%s' HttpOnly='%s'", this.name, this.value, this.domain, this.path, this.date.toString(), this.httpOnly);
  }
}
