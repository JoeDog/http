package org.joedog.http;

import java.net.URL;

public class HeaderFactoryImpl implements HeaderFactory {
  
  public HeaderFactoryImpl() {
  }

  public Response getResponse(double version) {
    if (version == 1.0) {
      return new v10Response();
    } 
    if (version == 1.1) {
      return new v11Response();
    }
    return null;
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

