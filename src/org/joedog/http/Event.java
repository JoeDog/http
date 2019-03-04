package org.joedog.http;

import java.net.URL;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Event {
  private Timestamp created       = null;
  private Timestamp modified      = null;
  private Timestamp visited       = null;
  private int       code          = 0;
  private URL       url           = null;
  private String    authorization = null;
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");  
  // System.out.println(sdf.format(timestamp));

  public Event () {
    this.created  = new Timestamp(System.currentTimeMillis());
    this.modified = new Timestamp(System.currentTimeMillis());
  }

  public void addUrl(URL url) {
    this.url = url;
    this.modified.setTime(System.currentTimeMillis());
  }

  public URL getUrl() {
    return this.url;
  }

  public boolean matches(String url) {
    return this.url.toString().equals(url);
  }

  public boolean matches(URL url) {
    return this.matches(url.toString());
  }

  public void setAuthorizationHeader(String authorization) {
    this.authorization = authorization;
    this.modified.setTime(System.currentTimeMillis());
  }

  public String getAuthorizationHeader() {
    return this.authorization;
  }

  public void setResult(int code) {
    this.code    = code;
    this.visited = new Timestamp(System.currentTimeMillis());
  }

  public int getCode() {
    return this.code;
  }
}
