package org.joedog.http;

public class Response <K, V> extends Headers {

  public static final String TRANSFER_ENCODING = "Transfer-Encoding";

  protected int    code       = 0;
  protected double version    = 1.0;
  protected String message    = null; 
  protected StringBuilder res = null;

  public Response() { 
    this.res = new StringBuilder();
  }

  public void add(String line) {
    this.res.append(line);
    this.res.append("\015\012");
    if (line.startsWith("HTTP/")) {
      this.parseResponse(line);
    }  
  }

  public String toString() {
    return this.res.toString();
  }

  private void parseResponse(String line) {
    String res[] = null;
    int count    = line.length() - line.replace(" ", "").length();   
    if (count < 2) {
      System.err.println("ERROR: Malformed Response");
      this.code    = 500;
      this.version = 1.1;
      return;
    } 
    res = line.split(" ", 3);
    this.version = Double.parseDouble(res[0].substring(5, res[0].length()));
    this.code    = Integer.parseInt(res[1]);
    this.message = res[2];
  }
}
