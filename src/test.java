import org.joedog.http.*;

public class test {
  public static void main(String [] args) {
    HTTP http = new HTTP();
    for (int i = 0; i < 5; i++) {
      http.get("https://www.joedog.org/siege/cookie-expire.php");
    } 
    //http.get("https://www.joedog.org/siege/cookie-expire.php");
    //http.get("https://www.joedog.org/siege/cookie-expire.php");
    //http.get("http://lisa.joedog.org/");
  }
}

