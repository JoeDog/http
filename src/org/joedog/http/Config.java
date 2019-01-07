package org.joedog.http;

import java.io.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.Comparator;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

public class Config extends Properties {
  private static final long serialVersionUID = 1L;
  private static Config     _instance        = null;
  private static Object     mutex            = new Object(); 
  private static Auth       auth             = null;

  private Config() {
    this.auth = new Auth();
  }

  public synchronized static Config getInstance() {
    if (_instance == null) {
      synchronized(mutex) {
        if (_instance == null) {
          _instance = new Config();
        }
      }
    }
    return _instance;
  }

  public String getAuthorizationHeader(Auth.TYPE type, String realm) {
    if (this.auth == null) return null;

    switch (type) {
      case DIGEST: 
      case NTLM: 
      case BASIC:  
      default:
        return auth.basicAuthorizationHeader(realm);
    }
  }

  public Enumeration<Object> keys() {
    Enumeration<Object> keysEnum = super.keys();
    Vector<Object> keyList = new Vector<Object>();

    while (keysEnum.hasMoreElements()) {
      keyList.add(keysEnum.nextElement());
    }

    Collections.sort(keyList, new Comparator<Object>() {
      @Override
      public int compare(Object o1, Object o2) {
        return o1.toString().compareTo(o2.toString());
      }
    });
    return keyList.elements();
  }

  @Override
  public void load(InputStream inStream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "ISO-8859-1"));
    String line;
 
    while ((line = reader.readLine()) != null) {
      char c = 0;
      int pos = 0;
      while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos)))
        pos++;

        if ((line.length() - pos) == 0 || line.charAt(pos) == '#' || line.charAt(pos) == '!')
          continue;

        int start = pos;
        boolean needsEscape = line.indexOf('\\', pos) != -1;
        StringBuilder key = needsEscape ? new StringBuilder() : null;
        while (pos < line.length()
          && ! Character.isWhitespace(c = line.charAt(pos++))
          && c != '=' && c != ':') {
          if (needsEscape && c == '\\') {
            if (pos == line.length()) {
              line = reader.readLine();
              if (line == null)
                line = "";
              pos = 0;
              while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos)))
                pos++;
            } else {
              c = line.charAt(pos++);
              switch (c) {
                case 'n':
                  key.append('\n');
                  break;
                case 't':
                  key.append('\t');
                  break;
                case 'r':
                  key.append('\r');
                  break;
                case 'u':
                  if (pos + 4 <= line.length()) {
                    char uni = (char) Integer.parseInt (line.substring(pos, pos + 4), 16);
                    key.append(uni);
                    pos += 4;
                  }
                  break;
                default:
                  key.append(c);
                  break;
              }
            }
          } else if (needsEscape) {
            key.append(c);
          }
        }
        boolean isDelim = (c == ':' || c == '=');
        String keyString;
        if (needsEscape)
          keyString = key.toString();
        else if (isDelim || Character.isWhitespace(c))
          keyString = line.substring(start, pos - 1);
        else
          keyString = line.substring(start, pos);
 
        while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos)))
          pos++;

        if (! isDelim && (c == ':' || c == '=')) {
          pos++;
          while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos)))
            pos++;
        }

        if (!needsEscape) {
          if (keyString.equals("authorization")) {
            this.auth.add(line.substring(pos));
          } else { 
            put(keyString, line.substring(pos));
          }
          continue;
        }
  
        StringBuilder element = new StringBuilder(line.length() - pos);
        /**
         * This parser is utilized only when the line is escaped
         */
        while (pos < line.length()) {
          c = line.charAt(pos++);
          if (c == '\\') {
            if (pos == line.length()) {
              line = reader.readLine();
  
              if (line == null)
                break;

              pos = 0;
              while (pos < line.length() && Character.isWhitespace(c = line.charAt(pos)))
                pos++;
              element.ensureCapacity(line.length() - pos + element.length());
            } else {
              c = line.charAt(pos++);
              switch (c) {
                case 'n':
                  element.append('\n');
                  break;
                case 't':
                  element.append('\t');
                  break;
                case 'r':
                  element.append('\r');
                  break;
                case 'u':
                  if (pos + 4 <= line.length()) {
                    char uni = (char) Integer.parseInt (line.substring(pos, pos + 4), 16);
                    element.append(uni);
                    pos += 4;
                  } 
                  break;
                default:
                  element.append(c);
                  break;
            }
          }
        } else {
          element.append(c);
        }
      }
      if (keyString.equals("authorization")) {
        this.auth.add(element.toString());
      } else {
        put(keyString, element.toString());
      }
    }
  }
}

