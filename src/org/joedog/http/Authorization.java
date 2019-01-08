package org.joedog.http;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.LinkedHashMap;

public class Authorization {
  public  enum TYPE { UNSUPPORTED, BASIC, DIGEST, NTLM; }
  private LinkedHashMap<String,Credentials> creds = new LinkedHashMap<String,Credentials>();
  
  public Authorization() {}

  public void add(String line) {
    Credentials c = new Credentials(line);
    this.creds.put(c.getRealm(), c);
  }

  public String basicAuthorizationHeader(String realm) {
    String clear   = null;
    String encoded = null;
    String header  = null;
    Credentials c  = this.getCredentials(realm);
    if (c == null) return null;

    clear  = String.format("%s:%s", c.getUsername(), c.getPassword());
    try {
      encoded = Base64.getEncoder().encodeToString(clear.getBytes("utf-8"));
    } catch(UnsupportedEncodingException ignore) {}
    header = String.format("Basic %s", encoded);
    return header;
  }

  private Credentials getCredentials(String realm) {
    Credentials c = creds.get(realm);

    if (c != null) {
      return c;
    }
      
    c = creds.get("all");

    return c;
  }
}
