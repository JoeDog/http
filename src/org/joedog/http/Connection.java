package org.joedog.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import javax.net.SocketFactory;
import javax.net.ssl.SSLParameters;
import java.net.SocketTimeoutException;
import javax.net.ssl.*;
import java.util.List;
import java.util.ArrayList;

public class Connection {
  private String  host      = null;
  private int     port      = 80;
  private Socket  sock      = null;
  private boolean secure    = false;
  private int timeout       = (15*1000);
  private PrintWriter    os = null;
  private BufferedReader is = null;

  public Connection(String host, int port) {
    this.host    = host;
    this.port    = port;
    this.secure  = false;
    this.open(host, port, secure);
  }

  public Connection(String host, int port, boolean secure) {
    this.host    = host;
    this.port    = port;
    this.secure  = secure;
    this.open(host, port, secure);
  }

  public Connection(String host, int port, int timeout, boolean secure) {
    this.host    = host;
    this.port    = port;
    this.secure  = secure;
    this.timeout = (timeout*1000);
    this.open(host, port, secure);
  }

  public boolean isConnected() {
    if (this.sock == null) { 
      return false;
    } else if (this.sock.isConnected()){
      return true;
    } else {
      try {
        this.sock.close();
      } catch (IOException ignored) {}
      this.sock = null;
      return false;
    }
  }

  public String readline() {
    String line = null;
    try {
      line = is.readLine();
      if (line == null)       return null; 
      if (line.length() == 0) return null;
      if (line.charAt(1) == '\r' || line.charAt(2) == '\n') {
        // we should probably never get here...
        return null; 
      }
    } catch (IOException ioe) {}
    return line;
  }

  public String read(int len) {
    int  i     = 0;
    char buf[] = new char[len];
    try {
      while (i < len) {
        char c = (char)is.read();
        if ((int)c == 0 || (int)c == 65535) { 
          break;
        }
        buf[i] = c;
        i++; 
      }
    } catch (IOException ioe) {}
    return String.valueOf(buf);
  }

  public boolean write(String msg) {
    if (!isConnected()) {
      return false;
    }
    os.print(msg);
    os.flush();
    return true;
  }
 
  private boolean open(String host, int port, boolean secure){
    this.sock = new Socket();
    try {
      if (secure) {
        try {
          SocketFactory factory  = null;;
          SSLParameters params   = new SSLParameters();
          SSLContext    context  = SSLContext.getDefault();
          List          sniNames = new ArrayList(1);
          sniNames.add(new SNIHostName(host));
          params.setServerNames(sniNames);
          factory = new SNISocketFactory(context.getSocketFactory(), params);
          this.sock = factory.createSocket(host, port);
        } catch (Exception e) {
          throw new IOException("SSL failure");
        }
      } else {
        InetAddress   iaddr = InetAddress.getByName(host);
        SocketAddress saddr = new InetSocketAddress(iaddr, port);
        this.sock.connect(saddr, this.timeout);
      }
      if (this.sock == null) {
        this.sock.close();
        throw new IllegalArgumentException("ERROR: Unable to connect to host: "+host);
      }
      this.os = new PrintWriter(
                new BufferedWriter(
                new OutputStreamWriter(this.sock.getOutputStream()))
      );
      this.is = new BufferedReader(
                new InputStreamReader(this.sock.getInputStream())
      );
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
