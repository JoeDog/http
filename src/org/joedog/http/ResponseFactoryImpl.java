package org.joedog.http;

import java.net.URL;

public class ResponseFactoryImpl implements ResponseFactory {
  
  public ResponseFactoryImpl() {
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
}

