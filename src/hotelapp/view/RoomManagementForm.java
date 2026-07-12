package hotelapp.view;

import hotelapp.dao.RoomDAO;
import hotelapp.model.Room;
import hotelapp.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Input UI - full CRUD screen for managing rooms.
 */
public class RoomManagementForm extends JFrame {

    private final RoomDAO roomDAO = new RoomDAO();

    private JTextField roomNumberField;
    private JComboBox<String> roomTypeCombo;
    private JTextField priceField;
    private JComboBox<String> statusCombo;
    private JTable roomTable;
    private DefaultTableModel tableModel;

    private Integer selectedRoomId = null; // null = adding a new room, not-null = editing

    public RoomManagementForm() {
        setTitle("Room Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        initComponents();
        loadRooms();
    }

    private void initComponents() {
        UITheme.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(UITheme.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---- form panel (top) ----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        formPanel.setBorder(UITheme.titledBorder("Room Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Room Number:"), gbc);
        roomNumberField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(roomNumberField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Room Type:"), gbc);
        roomTypeCombo = new JComboBox<>(new String[]{"SINGLE", "DOUBLE", "SUITE", "DELUXE"});
        gbc.gridx = 3; gbc.gridy = 0;
        formPanel.add(roomTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Price / Night (Rs.):"), gbc);
        priceField = new JTextField(10);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(priceField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Status:"), gbc);
        statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(statusCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(UITheme.CARD_BACKGROUND);
        JButton addBtn = new JButton("Add Room");
        UITheme.stylePrimaryButton(addBtn);
        JButton updateBtn = new JButton("Update Selected");
        UITheme.stylePrimaryButton(updateBtn);
        JButton deleteBtn = new JButton("Delete Selected");
        UITheme.styleDangerButton(deleteBtn);
        JButton clearBtn = new JButton("Clear Form");
        UITheme.styleSecondaryButton(clearBtn);
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // ---- table (center) ----
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Room No.", "Type", "Price/Night", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // table is read-only, edits happen through the form above
            }
        };
        roomTable = new JTable(tableModel);
        UITheme.styleTableHeader(roomTable);
        roomTable.getSelectionModel().addListSelectionListener(e -> onTableRowSelected());
        mainPanel.add(new JScrollPane(roomTable), BorderLayout.CENTER);

        add(mainPanel);

        addBtn.addActionListener(e -> addRoom());
        updateBtn.addActionListener(e -> updateRoom());
        deleteBtn.addActionListener(e -> deleteRoom());
        clearBtn.addActionListener(e -> clearForm());
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            for (Room r : rooms) {
                tableModel.addRow(new Object[]{
                        r.getRoomId(), r.getRoomNumber(), r.getRoomType(),
                        r.getPricePerNight(), r.getStatus()
                });
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void onTableRowSelected() {
        int row = roomTable.getSelectedRow();
        if (row == -1) return;

        selectedRoomId = (Integer) tableModel.getValueAt(row, 0);
        roomNumberField.setText(tableModel.getValueAt(row, 1).toString());
        roomTypeCombo.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        priceField.setText(tableModel.getValueAt(row, 3).toString());
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 4).toString());
    }

    /** Validates the form fields; returns the built Room, or null (and shows a message) if invalid. */
    private Room readAndValidateForm() {
        String roomNumber = roomNumberField.getText().trim();
        String priceText = priceField.getText().trim();

        if (roomNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Room number is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Price is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be greater than zero.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType((String) roomTypeCombo.getSelectedItem());
        room.setPricePerNight(price);
        room.setStatus((String) statusCombo.getSelectedItem());
        return room;
    }

    private void addRoom() {
        Room room = readAndValidateForm();
        if (room == null) return;

        try {
            roomDAO.addRoom(room);
            JOptionPane.showMessageDialog(this, "Room added successfully.");
            clearForm();
            loadRooms();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void updateRoom() {
        if (selectedRoomId == null) {
            JOptionPane.showMessageDialog(this, "Select a room from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Room room = readAndValidateForm();
        if (room == null) return;
        room.setRoomId(selectedRoomId);

        try {
            roomDAO.updateRoom(room);
            JOptionPane.showMessageDialog(this, "Room updated successfully.");
            clearForm();
            loadRooms();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void deleteRoom() {
        if (selectedRoomId == null) {
            JOptionPane.showMessageDialog(this, "Select a room from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this room? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            roomDAO.deleteRoom(selectedRoomId);
            JOptionPane.showMessageDialog(this, "Room deleted.");
            clearForm();
            loadRooms();
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void clearForm() {
        selectedRoomId = null;
        roomNumberField.setText("");
        priceField.setText("");
        roomTypeCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
        roomTable.clearSelection();
    }

    private void showDbError(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "Database error:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
