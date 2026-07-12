package hotelapp.dao;

import hotelapp.exception.RoomNotAvailableException;
import hotelapp.model.Booking;
import hotelapp.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO pattern) for the bookings table.
 * createBooking() is the "major transaction" required by the coursework:
 * it inserts a booking row AND flips the room's status to OCCUPIED as a
 * single database transaction (both succeed, or both roll back).
 */
public class BookingDAO {

    /**
     * Creates a new booking and marks the room as OCCUPIED.
     * Uses a JDBC transaction so the two updates succeed or fail together.
     *
     * @throws RoomNotAvailableException if the room is no longer available
     *                                    by the time this runs (e.g. someone
     *                                    else just booked it)
     */
    public void createBooking(Booking booking) throws SQLException, RoomNotAvailableException {
        Connection conn = DBConnection.getInstance().getConnection();

        try {
            conn.setAutoCommit(false);

            // 1. re-check the room is still available (avoids double-booking)
            String checkSql = "SELECT status FROM rooms WHERE room_id = ? FOR UPDATE";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, booking.getRoomId());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next() || !"AVAILABLE".equals(rs.getString("status"))) {
                        conn.rollback();
                        conn.setAutoCommit(true);
                        throw new RoomNotAvailableException("This room is no longer available. Please choose another room.");
                    }
                }
            }

            // 2. insert the booking
            String insertSql = "INSERT INTO bookings (customer_id, room_id, user_id, check_in_date, "
                    + "check_out_date, total_amount, status) VALUES (?, ?, ?, ?, ?, ?, 'CONFIRMED')";
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, booking.getCustomerId());
                insertPs.setInt(2, booking.getRoomId());
                insertPs.setInt(3, booking.getUserId());
                insertPs.setDate(4, Date.valueOf(booking.getCheckInDate()));
                insertPs.setDate(5, Date.valueOf(booking.getCheckOutDate()));
                insertPs.setBigDecimal(6, booking.getTotalAmount());
                insertPs.executeUpdate();
            }

            // 3. flip the room to OCCUPIED
            String updateRoomSql = "UPDATE rooms SET status = 'OCCUPIED' WHERE room_id = ?";
            try (PreparedStatement updatePs = conn.prepareStatement(updateRoomSql)) {
                updatePs.setInt(1, booking.getRoomId());
                updatePs.executeUpdate();
            }

            conn.commit();
        } catch (SQLException | RoomNotAvailableException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /** All bookings, joined with customer name and room number for display. */
    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, CONCAT(c.first_name, ' ', c.last_name) AS customer_name, r.room_number "
                + "FROM bookings b "
                + "JOIN customers c ON b.customer_id = c.customer_id "
                + "JOIN rooms r ON b.room_id = r.room_id "
                + "ORDER BY b.created_at DESC";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Booking b = new Booking();
                b.setBookingId(rs.getInt("booking_id"));
                b.setCustomerId(rs.getInt("customer_id"));
                b.setRoomId(rs.getInt("room_id"));
                b.setUserId(rs.getInt("user_id"));
                b.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                b.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                b.setTotalAmount(rs.getBigDecimal("total_amount"));
                b.setStatus(rs.getString("status"));
                b.setCustomerName(rs.getString("customer_name"));
                b.setRoomNumber(rs.getString("room_number"));
                bookings.add(b);
            }
        }
        return bookings;
    }

    /** Marks a booking as CHECKED_OUT and frees up the room again. */
    public void checkOutBooking(int bookingId, int roomId) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE bookings SET status = 'CHECKED_OUT' WHERE booking_id = ?")) {
                ps.setInt(1, bookingId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE rooms SET status = 'AVAILABLE' WHERE room_id = ?")) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
