import java.sql.*;
import java.util.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/internship_tracker";
    private static final String USER = "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD"); // match your actual password

    public static Connection getConnection() throws SQLException {
    if (PASSWORD == null) {
        throw new IllegalStateException("DB_PASSWORD environment variable is not set. See README for setup.");
    }
    return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    public static List<Application> getAll() throws SQLException {
        List<Application> list = new ArrayList<>();
        String sql = "SELECT * FROM applications ORDER BY applied_date DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Application(
                    rs.getInt("id"),
                    rs.getString("company_name"),
                    rs.getString("role"),
                    rs.getString("status"),
                    rs.getDate("applied_date").toString(),
                    rs.getDate("follow_up_date") != null ? rs.getDate("follow_up_date").toString() : null,
                    rs.getString("notes")
                ));
            }
        }
        return list;
    }

    public static boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE applications SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM applications WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public static int insert(Application app) throws SQLException {
        String sql = "INSERT INTO applications (company_name, role, status, applied_date, follow_up_date, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, app.companyName());
            ps.setString(2, app.role());
            ps.setString(3, app.status());
            ps.setString(4, app.appliedDate());
            ps.setString(5, app.followUpDate());
            ps.setString(6, app.notes());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }
}