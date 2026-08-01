import java.sql.Connection;
import java.sql.DriverManager;

public class DBTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/internship_tracker";
        String user = "root";        // change if your MySQL user is different
        String password = "Dhruv@123";        // put your MySQL root password here

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected successfully to: " + conn.getCatalog());
        } catch (Exception e) {
            System.out.println("Connection failed:");
            e.printStackTrace();
        }
    }
}