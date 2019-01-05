package org.joedog.http;

import java.net.URL;

public class v10Request extends Request {
  private String[] fields = {
    HOST,
    AUTHORIZATION,
    COOKIE,
    FROM,
    IF_MODIFIED_SINCE,
    REFERER,
    USER_AGENT 
  };

  public v10Request(URL url) {
    this.url     = url;
    this.version = 1.0;
    this.headers = fields;
    this.build();
  }
}
