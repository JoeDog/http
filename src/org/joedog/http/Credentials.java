package org.joedog.http;

public class Credentials {
  private String realm    = null;
  private String username = null;
  private String password = null;

  public Credentials(String line) {
    this.load(line);
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public String getRealm() {
    return this.realm;
  }

  public String toString() {
    String res = String.format("{%s}->{%s:%s}", this.realm, this.username, this.password);
    return res;
  }

  private void load(String line) {
    String res[] = null;
    int count = line.length() - line.replace(":", "").length();
    switch (count) {
      case 0:
        System.err.printf("ERROR: Malformed authorization line: %s\n", line);
        break;
      case 1:
        res           = line.split(":");
        this.realm    = "all";
        this.username = res[0];
        this.password = res[1]; 
        break;
      case 2:
        res           = line.split(":");
        this.realm    = res[2];
        this.username = res[0];
        this.password = res[1]; 
        break;
    }
  }
}
