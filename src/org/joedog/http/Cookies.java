package org.joedog.http;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@SuppressWarnings("serial")
public class Cookies implements Iterable<Cookie>  {
  private int cursor;
  private List<Cookie>           cookies   = null;
  private static final   String  SPECIAL   = "()<>@,;:\\\"/[]?={} \t";
  private static Cookies         _instance = null;
  private static Object          mutex     = new Object();

  private Cookies() {
    this.cursor  = 0;
    this.cookies = new ArrayList<Cookie>();
  }

  public synchronized static Cookies getInstance() {
    if (_instance == null) {
      synchronized(mutex) {
        if (_instance == null) {
          _instance = new Cookies();
        }
      }
    }
    return _instance;
  }

  public String getCookieHeader(URL url) {
    String host     = url.getHost();
    String path     = url.getPath();
    StringBuffer sb = new StringBuffer();
    for (Iterator<Cookie> iter = this.cookies.iterator(); iter.hasNext(); ) {
      Cookie c = iter.next();
      if (c.isExpired()) {
        iter.remove();
        continue;
      } 
      if (c.inPath(path) && c.inDomain(host)) {
        String buf  = (sb.length() > 1) ? " " : "";
        sb.append(c.getName()+"="+c.getValue()+";"+buf);
      }
    }
    return sb.toString();
  }

  /**
   * Parses cookie from a Set-Cookie header and places
   * a cookie object into this <String, Cookie>
   * Set-Cookie: __cfduid=d67fd1; expires=Thu, 09-Jan-20 20:55:05 GMT; path=/; domain=.joedog.org; HttpOnly
   * <p>
   * @param  String set-cookie
   * @return void
   */
  public void add(String line) {
    String  name     = null;
    String  value    = null;
    String  path     = null;
    String  domain   = null;
    String  expires  = null;
    boolean session  = false;
    boolean secure   = false;
    boolean httpOnly = false;
    Cookie  cookie   = null;
    String  parts[]; 

    line  = line.replaceAll("Set-Cookie: ", "");
    parts = line.split(";");
    if (parts.length > 0) {
      Pattern p = Pattern.compile("([^\\s=]*)=(.*)");
      Matcher m = p.matcher(parts[0]);
      if (m.matches()) {
        cookie = new Cookie(m.group(1), m.group(2));
        if (parts.length > 1) {
          for (int i = 1; i < parts.length; i++) {
            String tmp = parts[i].trim();
            if (tmp.contains("=")) {
              String [] pair = tmp.split("=");
              if (pair.length == 2) {
                if (pair[0].equals("expires")) cookie.setExpires(pair[1]); 
                if (pair[0].equals("domain"))  cookie.setDomain(pair[1]); 
                if (pair[0].equals("path"))    cookie.setPath(pair[1]); 
              }
            } else {
              if (tmp.equals("HttpOnly")) cookie.setHttpOnly(true);
            }
          }
        }
      }
    }
    if (! exists(cookie)) {
      this.add(cookie);
    }
  }

  public boolean exists(Cookie cookie) {
    for (Iterator<Cookie> iter = this.cookies.iterator(); iter.hasNext(); ) {
      Cookie c = iter.next();
      if (cookie.getName().equals(c.getName())) {
        if (c.inPath(cookie.getPath()) && c.inDomain(cookie.getDomain())) {
          c.setValue(cookie.getValue());
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Iterator<Cookie> iterator() {
    return new Iterator<Cookie> () {
      private final Iterator<Cookie> iter = cookies.iterator();

      @Override
      public boolean hasNext() {
        return iter.hasNext();
      }

      @Override
      public Cookie next() {
        return iter.next();
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("no changes allowed");
      }
    };
  } 

  public synchronized void add(Cookie cookie) {
    for (Iterator<Cookie> iter = this.cookies.iterator(); iter.hasNext(); ) {
      Cookie c = iter.next();
      if (c.getName().equals(cookie.getName())) {
        if (! c.isExpired()) {
           c.setValue(cookie.getValue());
           return;
        }
      }
    }
    cookies.add(cookie);
  } 

  public synchronized void remove(Cookie cookie) {
    for (Iterator<Cookie> iter = this.cookies.iterator(); iter.hasNext(); ) {
      Cookie c = iter.next();
      if (c.getName().equals(cookie.getName())) {
        iter.remove();
      }
    }
  }

  public String toString() {
    String ret = "";
    int i = 1;
    for (Cookie c : this) {
      if (c != null) {
        ret += i+".) "+c.toString()+"\n"; 
        i++;
      } 
    }
    return ret;
  }
}
