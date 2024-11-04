import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JdbcTest {
  private String url;
  private String userName;
  private String pwd;

  public Connection getConnection() {
    Properties prop = new Properties();
    try (InputStream input = new FileInputStream("config.properties")) {
      prop.load(input);
      url = prop.getProperty("db.url");
      userName = prop.getProperty("db.user");
      pwd = prop.getProperty("db.password");
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      Connection conn = DriverManager.getConnection(url, userName, pwd);
      if (conn != null) {
        System.out.println("connect suc");
        return conn;
      } else {
        System.out.println("connect fail");
        return null;
      }
    } catch (Exception e) {
      System.out.println("connect fail: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }
}
