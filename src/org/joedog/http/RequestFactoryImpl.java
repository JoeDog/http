package org.joedog.http;

import java.net.URL;

public class RequestFactoryImpl implements RequestFactory {
  
  public RequestFactoryImpl() {
  }

  public Request getRequest(URL url, double version) {
    if (version == 1.0) {
      return new v10Request(url);
    } 
    if (version == 1.1) {
      return new v11Request(url);
    }
    return null;
  }
}

