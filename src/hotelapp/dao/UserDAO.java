package hotelapp.dao;

import hotelapp.exception.InvalidLoginException;
import hotelapp.model.User;
import hotelapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO design pattern) for the users table.
 * Keeps ALL SQL for "users" in one place, separate from the UI (MVC).
 */
public class UserDAO {

    /**
     * Validates the given credentials against the database.
     *
     * @return the matching User if credentials are correct
     * @throws InvalidLoginException if username/password do not match
     * @throws SQLException          if a database error occurs
     */
    public User validateLogin(String username, String password)
            throws InvalidLoginException, SQLException {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("full_name"),
                            rs.getString("role")
                    );
                } else {
                    throw new InvalidLoginException("Invalid username or password.");
                }
            }
        }
    }
}
