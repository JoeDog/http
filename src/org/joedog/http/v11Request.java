package org.joedog.http;

import java.net.URL;

public class v11Request extends Request {
  private String[] fields = {
    HOST,
    AUTHORIZATION,
    COOKIE,
    FROM,
    IF_MODIFIED_SINCE,
    REFERER,
    USER_AGENT,
    ACCEPT,
    ACCEPT_CHARSET,
    ACCEPT_ENCODING,
    ACCEPT_LANGUAGE,
    AUTHORIZATION,
    CONNECTION,
    EXPECT,
    IF_MATCH,
    IF_NONE_MATCH,
    IF_RANGE,
    IF_UNMODIFIED_SINCE,
    MAX_FORWARDS,
    PROXY_AUTHORIZATION,
    RANGE,
    TE
  };

  public v11Request(URL url) {
    this.url     = url;
    this.version = 1.1;
    this.headers = fields;
    this.build();
  }
}
