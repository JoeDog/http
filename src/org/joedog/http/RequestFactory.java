package org.joedog.http;

import java.net.URL;

public interface RequestFactory {
  public Request getRequest(URL url, double version);
}

