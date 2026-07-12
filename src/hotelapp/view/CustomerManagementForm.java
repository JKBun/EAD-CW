package hotelapp.view;

import hotelapp.dao.CustomerDAO;
import hotelapp.model.Customer;
import hotelapp.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Input UI - full CRUD screen for managing customers / guests.
 */
public class CustomerManagementForm extends JFrame {

    private final CustomerDAO customerDAO = new CustomerDAO();

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField nicField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    private Integer selectedCustomerId = null;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public CustomerManagementForm() {
        setTitle("Customer Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);

        initComponents();
        loadCustomers();
    }

    private void initComponents() {
        UITheme.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(UITheme.BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ---- form panel (top) ----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.CARD_BACKGROUND);
        formPanel.setBorder(UITheme.titledBorder("Customer Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        firstNameField = new JTextField(12);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Last Name:"), gbc);
        lastNameField = new JTextField(12);
        gbc.gridx = 3; gbc.gridy = 0;
        formPanel.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("NIC / Passport:"), gbc);
        nicField = new JTextField(12);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(nicField, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        phoneField = new JTextField(12);
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);
        emailField = new JTextField(12);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(emailField, gbc);

        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(new JLabel("Address:"), gbc);
        addressField = new JTextField(12);
        gbc.gridx = 3; gbc.gridy = 2;
        formPanel.add(addressField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(UITheme.CARD_BACKGROUND);
        JButton addBtn = new JButton("Add Customer");
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

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // ---- table (center) ----
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "First Name", "Last Name", "NIC/Passport", "Phone", "Email", "Address"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        UITheme.styleTableHeader(customerTable);
        customerTable.getSelectionModel().addListSelectionListener(e -> onTableRowSelected());
        mainPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        add(mainPanel);

        addBtn.addActionListener(e -> addCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        clearBtn.addActionListener(e -> clearForm());
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{
                        c.getCustomerId(), c.getFirstName(), c.getLastName(),
                        c.getNicPassport(), c.getPhone(), c.getEmail(), c.getAddress()
                });
            }
        } catch (SQLException ex) {
            showDbError(ex);
        }
    }

    private void onTableRowSelected() {
        int row = customerTable.getSelectedRow();
        if (row == -1) return;

        selectedCustomerId = (Integer) tableModel.getValueAt(row, 0);
        firstNameField.setText(str(tableModel.getValueAt(row, 1)));
        lastNameField.setText(str(tableModel.getValueAt(row, 2)));
        nicField.setText(str(tableModel.getValueAt(row, 3)));
        phoneField.setText(str(tableModel.getValueAt(row, 4)));
        emailField.setText(str(tableModel.getValueAt(row, 5)));
        addressField.setText(str(tableModel.getValueAt(row, 6)));
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }

    private Customer readAndValidateForm() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String nic = nicField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First and last name are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (nic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIC / Passport number is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (phone.isEmpty() || !phone.matches("\\d{9,15}")) {
            JOptionPane.showMessageDialog(this, "Enter a valid phone number (digits only, 9-15 characters).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!email.isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address, or leave it blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        Customer c = new Customer();
        c.setFirstName(firstName);
        c.setLastName(lastName);
        c.setNicPassport(nic);
        c.setPhone(phone);
        c.setEmail(email);
        c.setAddress(address);
        return c;
    }

    private void addCustomer() {
        Customer c = readAndValidateForm();
        if (c == null) return;

        try {
            customerDAO.addCustomer(c);
            JOptionPane.showMessageDialog(this, "Customer added successfully.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            if (isDuplicateEntryError(ex)) {
                JOptionPane.showMessageDialog(this, "A customer with this NIC/Passport already exists.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } else {
                showDbError(ex);
            }
        }
    }

    private void updateCustomer() {
        if (selectedCustomerId == null) {
            JOptionPane.showMessageDialog(this, "Select a customer from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Customer c = readAndValidateForm();
        if (c == null) return;
        c.setCustomerId(selectedCustomerId);

        try {
            customerDAO.updateCustomer(c);
            JOptionPane.showMessageDialog(this, "Customer updated successfully.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            if (isDuplicateEntryError(ex)) {
                JOptionPane.showMessageDialog(this, "Another customer already has this NIC/Passport.", "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
            } else {
                showDbError(ex);
            }
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId == null) {
            JOptionPane.showMessageDialog(this, "Select a customer from the table first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this customer? This cannot be undone.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            customerDAO.deleteCustomer(selectedCustomerId);
            JOptionPane.showMessageDialog(this, "Customer deleted.");
            clearForm();
            loadCustomers();
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("foreign key")) {
                JOptionPane.showMessageDialog(this,
                        "This customer has existing bookings and cannot be deleted.",
                        "Cannot Delete", JOptionPane.WARNING_MESSAGE);
            } else {
                showDbError(ex);
            }
        }
    }

    private void clearForm() {
        selectedCustomerId = null;
        firstNameField.setText("");
        lastNameField.setText("");
        nicField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        customerTable.clearSelection();
    }

    private boolean isDuplicateEntryError(SQLException ex) {
        return ex.getMessage() != null && ex.getMessage().toLowerCase().contains("duplicate entry");
    }

    private void showDbError(SQLException ex) {
        JOptionPane.showMessageDialog(this,
                "Database error:\n" + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
