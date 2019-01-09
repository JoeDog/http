package org.joedog.http;

import java.net.URL;

public interface HeaderFactory {
  public Response getResponse(double version);
  public Request getRequest(URL url, double version);  
}

