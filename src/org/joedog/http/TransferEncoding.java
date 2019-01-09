package org.joedog.http;

public class TransferEncoding {
  public  enum TYPE { NONE, FIXED, CHUNKED; }
  private boolean more   = true;
  private int     length = 0;

  public TransferEncoding() {
    
  }

  public boolean hasMore() {
    return this.more;
  }

  public int length(Connection conn) {
    if (this.length > 0) {
      return this.length;
    }
    int    len = 0;
    String buf = null;
    while (buf == null) {
      buf = conn.readline();
    }
    try {
      len = Integer.parseInt(buf, 16);
    } catch (java.lang.NumberFormatException ignore) {}

    if (len == 0) {
      this.more = false;
    }
    return len;
  }
}
