package hotelapp.dao;

import hotelapp.exception.InvalidLoginException;
import hotelapp.model.User;
import hotelapp.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

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
