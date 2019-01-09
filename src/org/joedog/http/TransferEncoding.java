package org.joedog.http;

public class TransferEncoding {
  public  enum TYPE { NONE, FIXED, CHUNKED; }
  private boolean more   = true;
  private int     length = 0;
  private TYPE    type;

  public TransferEncoding(Response response) {
    this.type   = response.getTransferEncoding(); 
    this.length = response.getContentLength();
    if (this.length > 0) {
      // One must be so careful these days...
      this.type = TYPE.FIXED;
    }
  }

  public void noMore() {
    this.more = false;
  }

  public boolean hasMore() {
    return this.more;
  }

  public int length(Connection conn) {
    switch (this.type) {
      case FIXED: 
        if (this.length > 0) {
          this.more = false;
          return this.length;
        }
      case CHUNKED:
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
      case NONE: 
      default:
        if (conn.isClosed()) {
          this.more = false;
          return 0;
        }
        return 1;
    }
  }
}
