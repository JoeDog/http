package org.joedog.http;

import java.net.URL;
import java.util.Stack;

public class History {
  private Stack<Event> events;

  public History() {
    this.events = new Stack<Event>();
  }

  public void addEvent(Event event){
    events.addElement(event);
  }

  public boolean contains(URL url) {
    if (url == null) return false;

    for (Event e : this.events) {
      if (e.getUrl().toString().equals(url.toString())) {
        return true;
      }
    } 
    return false;
  }

  public Event getEvent(String url) {
    if (url == null || url.length() < 1) return new Event();

    for (Event e : this.events) {
      if (e.getUrl().toString().equals(url)) {
        return e;
      }
    } 
    return new Event();
  }

  public Event getEvent(URL url) {
    return this.getEvent(url.toString()); // place holder
  }
}
