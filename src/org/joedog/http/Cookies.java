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
    for (Cookie c : this) {
      if ((path.startsWith(c.getPath())) && (host.endsWith(c.getDomain()))) {
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
        this.add(cookie);
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
        } else {
          //System.out.printf("PARTS LESS THAN 2) LINE: '%s'\n", line); 
        }
      } else {
        //System.out.printf(">>>>>>>>>>>>>>>>>>>>>>>> PART[0]: %s\n", parts[0]); 
      }
    }
    /*Pattern pattern = Pattern.compile("([^=]+)=([^\\;]*);?\\s?");
    Matcher matcher = pattern.matcher(line);
    while (matcher.find()) {
      int count = matcher.groupCount();
      if (count == 2) {
        if (matcher.group(1).equals("expires")) {
          expires = matcher.group(2);
        } else if (matcher.group(1).equals("path")) {
          path = matcher.group(2);
        } else if (matcher.group(1).equals("domain")) {
          domain = matcher.group(2);
        } else {
          name  = matcher.group(1);
          value = matcher.group(2);
        }
      }
      for (int i = 0; i <= count; ++i) {
        System.out.println("group[" + i + "]=" + matcher.group(i));
      }
      name  = matcher.group(1);
      value = matcher.group(2);
    }*/
    System.out.printf("COOKIE: %s\n", cookie.toString());
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
        iter.remove();
      }
    }
    cookies.add(cookie);
  } 

  /**
   * Largely stolen from the public domain.
   * Parses cookie[s] from a Set-Cookie header and places
   * a cookie object into this <String, Cookie>
   * Set-Cookie: __cfduid=d67fd1; expires=Thu, 09-Jan-20 20:55:05 GMT; path=/; domain=.joedog.org; HttpOnly
   * @Author: unknown
   * <p>
   * @param  String cookie_header
   * @return void
   */
/*************************************************************************
  public void add(String header) {
    Cookie          cookie    = null;
    String          key       = null;
    String          name      = null;
    String          value     = null;
    String          token     = null;
    StringTokenizer tokenizer = null;

    String tmp = header.replaceFirst("^Set-Cookie: ", "");
    tokenizer  = new StringTokenizer(tmp, ";,", true);
    
    while (tokenizer.hasMoreTokens()) {
      token = tokenizer.nextToken().trim();
      if (token.equals (";")) { 
        continue;
      }
      if (token.equals(",")) {
        // need a second cookie
        name   = null;
        value  = null;
        cookie = null;
        continue;
      }
      
      int i = token.indexOf('=');    
      if (i == -1) {
        // This is a token like HttpOnly which is not in key=val format
        name  = token;
        value = null;  
        key   = name.toLowerCase();
      } else {
        name  = token.substring (0, i);
        value = token.substring (i + 1);
        key   = name.toLowerCase();
      }

      if (cookie == null) {
        cookie = new Cookie(name, value);
        this.add(cookie);
      } else {
        if (key.equals("expires")) {
          String c = "";
          String m = "";
          if (value.length() < 4) {
            c = tokenizer.nextToken();
            m = tokenizer.nextToken();
          }
          value = String.format("%s%s%s", value, c, m);
        } else if (key.equals("path")) {
          if (value != null) {
            cookie.setPath(value);
          }
        } else if (key.equals("domain")) {
          if (value != null) {
            cookie.setDomain(value);
          }
        } else if (key.equals("secure")) {
          //System.out.println("secure");
        } else if (key.equals("comment")) {
          //System.out.println("comment: "+value);
        } else if (key.equals("version")) {
          //System.out.println("version: "+value);
        } else if (key.equals("max-age")) {
          //System.out.println("max-age: "+value);
        } else {
          if (key.equals("httponly")) {   
            //System.out.println("HttpOnly");
          }
        }
      }
    } 
  }
*************************************************************************/

  private boolean isToken (String value) {
    int len;
    char c;
    boolean ret = true;

    len = value.length ();
    for (int i = 0; i < len && ret; i++) {
      c = value.charAt (i);
      if (c < ' ' || c > '~' || SPECIAL.indexOf (c) != -1) {
        ret = false;
      }
    }
    return (ret);
  }
}
