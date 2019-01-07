package org.joedog.http;

import java.util.LinkedHashMap;

public class Auth {
  public  enum TYPE { UNSUPPORTED, BASIC, DIGEST, NTLM; }
  private LinkedHashMap<String,Credentials> creds = new LinkedHashMap<String,Credentials>();
  
  public Auth() {}

  public void add(String line) {
    Credentials c = new Credentials(line);
    this.creds.put(c.getRealm(), c);
  }

  public Credentials getCredentials(String realm) {
    if (creds.get(realm) != null) {
      return creds.get(realm);
    }

    if (creds.get("all") != null) {
      return creds.get("all");
    }

    return null;
  }
}
