package org.joedog.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.Socket;
import java.net.SocketAddress;
import javax.net.SocketFactory;
import javax.net.ssl.SSLParameters;
import java.net.SocketTimeoutException;
import javax.net.ssl.*;
import java.util.List;
import java.util.ArrayList;

public class Connection {
  public enum TYPE { CLOSE, KEEP_ALIVE; }

  private String  host      = null;
  private int     port      = 80;
  private Socket  sock      = null;
  private boolean secure    = false;
  private boolean closed    = true;
  private int timeout       = (15*1000);
  private PrintWriter    os = null;
  private BufferedReader is = null;

  public Connection() {
    // a stub - we'll rely on open()
  }

  public Connection(String host, int port) {
    this.host    = host;
    this.port    = port;
    this.secure  = false;
  }

  public Connection(String host, int port, boolean secure) {
    this.host    = host;
    this.port    = port;
    this.secure  = secure;
  }

  public Connection(String host, int port, int timeout, boolean secure) {
    this.host    = host;
    this.port    = port;
    this.timeout = (timeout*1000);
    this.secure  = secure;
  }

  public boolean open(URL url) {
    this.host    = url.getHost();
    this.port    = (url.getPort() > 0) ? url.getPort() : url.getDefaultPort();  
    this.secure  = (url.getProtocol().equals("https")) ? true : false;
    return this.open();
  }

  public boolean open(String host, int port, boolean secure) {
    this.host    = host;
    this.port    = port;
    this.secure  = secure;
    return this.open();
  }

  public boolean open(String host, int port, int timeout, boolean secure) {
    this.host    = host;
    this.port    = port;
    this.timeout = (timeout*1000);
    this.secure  = secure;
    return this.open();
  }

  public boolean isClosed() {
    if (this.os   == null) {
      return true;
    }
    if (this.is   == null) {
      return true;
    }
    if (this.sock == null) {
      return true;
    }
    return this.closed;
  }

  public synchronized boolean close() {
    try {
      if (this.is != null) {
        this.is.close();
        this.is = null;
      }
      if (this.os != null) {
        this.os.close();
        this.os = null;
      }
      if (! this.sock.isClosed()) {
        this.sock.close();
        this.sock = null;
      }
      this.closed = true;
      return this.closed;
    } catch (IOException ignore) {}
    return false;
  }

  public synchronized String readline() {
    String line = null;
    try {
      line = is.readLine();
      if (line == null)       return null; 
      if (line.length() == 0) return null;
      if (line.length() == 1) { 
        // Added for chunked encoding.
        return line;
      }
      if (line.length() == 2) {
        if (line.charAt(0) == '\r' || line.charAt(1) == '\n') {
          // we should probably never get here...
          return null; 
        }
      }
    } catch (IOException ioe) {}
    return line;
  }

  /*public synchronized String read(int len) {
    int  i     = 0;
    char buf[] = new char[len];
    try {
      while (i < len) {
        char c = (char)is.read();
        if ((int)c == 0 || (int)c == 65535) { 
          return null;
        }
        buf[i] = c;
        i++; 
      }
    } catch (IOException ioe) {}
    return String.valueOf(buf);
  }*/

  public synchronized String read(int len) {
    int  i     = 0;
    byte buf[] = new byte[len];
    try {
      while (i < len) {
        byte b = (byte)is.read();
        if (b == -1) { 
          return null;
        }
        buf[i] = b;
        i++; 
      }
    } catch (IOException ioe) {}
    return asString(buf);
  }

  private String asString(byte[] bytes) {
    String res = "";
    for (byte b : bytes) {
      res += (char)b;
    }
    return res;
  }

  public synchronized boolean write(String msg) {
    if (this.closed) {
      return false;
    }
    os.print(msg);
    os.flush();
    return true;
  }

  private synchronized boolean open(){
    if (! this.isClosed()) {
      return true;
    }
    this.sock = new Socket();
    try {
      if (this.secure) {
        try {
          SocketFactory factory  = null;;
          SSLParameters params   = new SSLParameters();
          SSLContext    context  = SSLContext.getDefault();
          List          sniNames = new ArrayList(1);
          sniNames.add(new SNIHostName(this.host));
          params.setServerNames(sniNames);
          factory = new SNISocketFactory(context.getSocketFactory(), params);
          this.sock = factory.createSocket(this.host, this.port);
        } catch (Exception e) {
          throw new IOException("SSL failure");
        }
      } else {
        InetAddress   iaddr = InetAddress.getByName(this.host);
        SocketAddress saddr = new InetSocketAddress(iaddr, port);
        this.sock.connect(saddr, this.timeout);
      }
      if (this.sock == null) {
        this.sock.close();
        throw new IllegalArgumentException("ERROR: Unable to connect to host: "+this.host);
      }
      this.os = new PrintWriter(
                new BufferedWriter(
                new OutputStreamWriter(this.sock.getOutputStream()))
      );
      this.is = new BufferedReader(
                new InputStreamReader(this.sock.getInputStream())
      );
      this.closed = false;
      return true;
    } catch (SocketTimeoutException ste) {
      System.err.println("ERROR: Timed out waiting for the socket.");
      ste.printStackTrace();
    } catch (IOException ioe) {
      System.err.println("ERROR: Unable to open I/O streams.");
      ioe.printStackTrace();
    } catch (Exception e) {
      System.err.println("ERROR: Unknown problem.");
      e.printStackTrace();
    }
    return false;
  }
}
