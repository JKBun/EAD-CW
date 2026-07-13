package hotelapp.dao;

import hotelapp.model.Customer;
import hotelapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CustomerDAO {

    public void addCustomer(Customer c) throws SQLException {
        String sql = "INSERT INTO customers (first_name, last_name, nic_passport, phone, email, address) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getNicPassport());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getAddress());
            ps.executeUpdate();
        }
    }

    public void updateCustomer(Customer c) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, nic_passport = ?, "
                + "phone = ?, email = ?, address = ? WHERE customer_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getFirstName());
            ps.setString(2, c.getLastName());
            ps.setString(3, c.getNicPassport());
            ps.setString(4, c.getPhone());
            ps.setString(5, c.getEmail());
            ps.setString(6, c.getAddress());
            ps.setInt(7, c.getCustomerId());
            ps.executeUpdate();
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        }
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY first_name";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                customers.add(mapRow(rs));
            }
        }
        return customers;
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("customer_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("nic_passport"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address")
        );
    }
}
