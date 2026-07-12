package hotelapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton design pattern - ensures only ONE database connection
 * object is created and reused across the whole application.
 *
 * Update DB_URL / DB_USER / DB_PASSWORD to match your local MySQL setup.
 */
public class DBConnection {

    private static final String DB_URL =
            //** NOTE-The port number has been changed do to errors on my ports.
            "jdbc:mysql://localhost:3307/hotel_management_system?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // <-- put your MySQL root password here

    private static DBConnection instance;
    private Connection connection;

    // private constructor -> nobody outside this class can "new" it
    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. "
                    + "Make sure mysql-connector-j jar is added to the project libraries.", e);
        }
    }

    public static DBConnection getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
