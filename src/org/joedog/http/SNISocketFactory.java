package org.joedog.http;
/**
 * Author:  Girish Kamath
 * Source:  http://javabreaks.blogspot.com/2015/12/java-ssl-handshake-with-server-name.html
 * Licence: Public domain
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SNISocketFactory extends SSLSocketFactory {
  private final SSLSocketFactory factory;
  private final SSLParameters    params;

  public SNISocketFactory(SSLSocketFactory factory, SSLParameters params) {
    this.factory = factory;
    this.params  = params;
  }

  @Override
  public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
    SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
    setParameters(socket);
    return socket;
  }

  @Override
  public Socket createSocket(String host, int port, InetAddress localHost, int localPort)
    throws IOException, UnknownHostException {
    SSLSocket socket = (SSLSocket) factory.createSocket(host, port, localHost, localPort);
    setParameters(socket);
    return socket;
  }

  @Override
  public Socket createSocket(InetAddress host, int port) throws IOException {
    SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
    setParameters(socket);
    return socket;
  }

  @Override
  public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) 
  throws IOException {
    SSLSocket socket = (SSLSocket) factory.createSocket(address, port, localAddress, localPort);
    setParameters(socket);
    return socket;
  }

  @Override
  public Socket createSocket() throws IOException {
    SSLSocket socket = (SSLSocket) factory.createSocket();
    setParameters(socket);
    return socket;
  }

  @Override
  public String[] getDefaultCipherSuites() {
    return factory.getDefaultCipherSuites();
  }

  @Override
  public String[] getSupportedCipherSuites() {
    return factory.getSupportedCipherSuites();
  }

  @Override
  public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
    SSLSocket socket = (SSLSocket) factory.createSocket(s, host, port, autoClose);
    setParameters(socket);
    return socket;
  }

  private void setParameters(SSLSocket socket) {
    socket.setSSLParameters(params);
  }
}

