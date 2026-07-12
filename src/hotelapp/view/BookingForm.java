package hotelapp.view;

import hotelapp.dao.BookingDAO;
import hotelapp.dao.CustomerDAO;
import hotelapp.dao.RoomDAO;
import hotelapp.exception.RoomNotAvailableException;
import hotelapp.model.Booking;
import hotelapp.model.Customer;
import hotelapp.model.Room;
import hotelapp.model.User;
import hotelapp.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Transaction UI - the main functionality of the system.
 * Creates a booking that links a Customer + a Room + the logged-in staff User,
 * calculates the total automatically, and updates the room's availability.
 */
public class BookingForm extends JFrame {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final User currentUser;

    private JComboBox<Customer> customerCombo;
    private JComboBox<Room> roomCombo;
    private JSpinner checkInSpinner;
    private JSpinner checkOutSpinner;
    private JLabel totalLabel;
    private JTable bookingTable;
    private DefaultTableModel tableModel;

    public BookingForm(User currentUser) {
        this.currentUser = currentUser;

        setTitle("New Booking");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadCustomersAndRooms();
        loadBookings();
    }

    private void initComponents() {
        UITheme.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(UITheme.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---- form panel (top) ----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        formPanel.setBorder(UITheme.titledBorder("Create Booking"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Customer:"), gbc);
        customerCombo = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(customerCombo, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Room (available only):"), gbc);
        roomCombo = new JComboBox<>();
        roomCombo.addActionListener(e -> recalculateTotal());
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(roomCombo, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Check-in Date:"), gbc);
        checkInSpinner = new JSpinner(new SpinnerDateModel());
        checkInSpinner.setEditor(new JSpinner.DateEditor(checkInSpinner, "yyyy-MM-dd"));
        checkInSpinner.setValue(new Date());
        checkInSpinner.addChangeListener(e -> recalculateTotal());
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(checkInSpinner, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(new JLabel("Check-out Date:"), gbc);
        checkOutSpinner = new JSpinner(new SpinnerDateModel());
        checkOutSpinner.setEditor(new JSpinner.DateEditor(checkOutSpinner, "yyyy-MM-dd"));
        checkOutSpinner.setValue(new Date(System.currentTimeMillis() + 86_400_000L)); // tomorrow
        checkOutSpinner.addChangeListener(e -> recalculateTotal());
        gbc.gridx = 3; gbc.gridy = 2;
        formPanel.add(checkOutSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Total Amount:"), gbc);
        totalLabel = new JLabel("Rs. 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(UITheme.SUCCESS);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(totalLabel, gbc);

        JButton confirmBtn = new JButton("Confirm Booking");
        UITheme.stylePrimaryButton(confirmBtn);
        confirmBtn.addActionListener(e -> confirmBooking());
        JButton refreshBtn = new JButton("Refresh Rooms/Customers");
        UITheme.styleSecondaryButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadCustomersAndRooms());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(UITheme.CARD_BACKGROUND);
        buttonPanel.add(confirmBtn);
        buttonPanel.add(refreshBtn);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // ---- bookings table (center) ----
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Customer", "Room", "Check-in", "Check-out", "Total", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        bookingTable = new JTable(tableModel);
        UITheme.styleTableHeader(bookingTable);
        JScrollPane scrollPane = new JScrollPane(bookingTable);
        scrollPane.setBorder(UITheme.titledBorder("All Bookings"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton checkOutBtn = new JButton("Check Out Selected Booking");
        UITheme.stylePrimaryButton(checkOutBtn);
        checkOutBtn.addActionListener(e -> checkOutSelected());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UITheme.BACKGROUND);
        bottomPanel.add(checkOutBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadCustomersAndRooms() {
        try {
            customerCombo.removeAllItems();
            for (Customer c : customerDAO.getAllCustomers()) {
                customerCombo.addItem(c);
            }
            roomCombo.removeAllItems();
            for (Room r : roomDAO.getAvailableRooms()) {
                roomCombo.addItem(r);
            }
            recalculateTotal();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void loadBookings() {
        tableModel.setRowCount(0);
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            for (Booking b : bookings) {
                tableModel.addRow(new Object[]{
                        b.getBookingId(), b.getCustomerName(), b.getRoomNumber(),
                        b.getCheckInDate(), b.getCheckOutDate(), b.getTotalAmount(), b.getStatus()
                });
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private LocalDate spinnerToLocalDate(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    private void recalculateTotal() {
        Room selectedRoom = (Room) roomCombo.getSelectedItem();
        if (selectedRoom == null) {
            totalLabel.setText("Rs. 0.00");
            return;
        }
        long nights = calculateNights();
        if (nights <= 0) {
            totalLabel.setText("Rs. 0.00 (invalid dates)");
            return;
        }
        BigDecimal total = selectedRoom.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        totalLabel.setText(String.format("Rs. %,.2f  (%d night%s)", total, nights, nights == 1 ? "" : "s"));
    }

    private long calculateNights() {
        try {
            LocalDate checkIn = spinnerToLocalDate(checkInSpinner);
            LocalDate checkOut = spinnerToLocalDate(checkOutSpinner);
            return ChronoUnit.DAYS.between(checkIn, checkOut);
        } catch (Exception ex) {
            return 0;
        }
    }

    private void confirmBooking() {
        Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
        Room selectedRoom = (Room) roomCombo.getSelectedItem();

        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "No customers found. Add a customer first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedRoom == null) {
            JOptionPane.showMessageDialog(this, "No available rooms. Check Room Management.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate checkIn = spinnerToLocalDate(checkInSpinner);
        LocalDate checkOut = spinnerToLocalDate(checkOutSpinner);
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);

        if (nights <= 0) {
            JOptionPane.showMessageDialog(this, "Check-out date must be after check-in date.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal total = selectedRoom.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setCustomerId(selectedCustomer.getCustomerId());
        booking.setRoomId(selectedRoom.getRoomId());
        booking.setUserId(currentUser.getUserId());
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setTotalAmount(total);

        try {
            bookingDAO.createBooking(booking);
            JOptionPane.showMessageDialog(this,
                    "Booking confirmed for " + selectedCustomer.getFullName()
                            + " in room " + selectedRoom.getRoomNumber() + ".\nTotal: Rs. " + total,
                    "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
            loadCustomersAndRooms(); // the booked room drops out of the available list
            loadBookings();
        } catch (RoomNotAvailableException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Room Not Available", JOptionPane.WARNING_MESSAGE);
            loadCustomersAndRooms();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void checkOutSelected() {
        int row = bookingTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a booking from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String status = (String) tableModel.getValueAt(row, 6);
        if ("CHECKED_OUT".equals(status)) {
            JOptionPane.showMessageDialog(this, "This booking is already checked out.");
            return;
        }

        int bookingId = (Integer) tableModel.getValueAt(row, 0);
        String roomNumber = (String) tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Check out booking #" + bookingId + " (Room " + roomNumber + ")?",
                "Confirm Check-out", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // find the room_id behind this room number to free it up
            Room matchingRoom = null;
            for (Room r : roomDAO.getAllRooms()) {
                if (r.getRoomNumber().equals(roomNumber)) {
                    matchingRoom = r;
                    break;
                }
            }
            if (matchingRoom == null) {
                JOptionPane.showMessageDialog(this, "Could not find that room.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            bookingDAO.checkOutBooking(bookingId, matchingRoom.getRoomId());
            JOptionPane.showMessageDialog(this, "Checked out. Room is now available again.");
            loadCustomersAndRooms();
            loadBookings();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void showDbError(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "Database error:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
