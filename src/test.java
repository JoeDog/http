import org.joedog.http.*;

public class test {
  public static void main(String [] args) {
    HTTP http = new HTTP();
    http.get("https://www.joedog.org/secret/");
    //http.get("http://lisa.joedog.org/");
  }
}

