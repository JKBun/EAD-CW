package hotelapp.view;

import hotelapp.dao.UserDAO;
import hotelapp.exception.InvalidLoginException;
import hotelapp.model.User;
import hotelapp.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Login form - the entry point of the application.
 * On successful login, opens the DashboardForm and passes the
 * logged-in User object to it (so the dashboard knows the role/name).
 */
public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel statusLabel;

    private final UserDAO userDAO = new UserDAO();

    public LoginForm() {
        setTitle("Hotel Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        UITheme.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND);

        JLabel titleLabel = UITheme.titleBanner("Hotel Management System");
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(UITheme.TEXT_DARK);
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField(16);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(UITheme.TEXT_DARK);
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(16);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(UITheme.DANGER);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(UITheme.BACKGROUND);
        loginButton = new JButton("Login");
        UITheme.stylePrimaryButton(loginButton);
        exitButton = new JButton("Exit");
        UITheme.styleSecondaryButton(exitButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        JLabel testAccountsLabel = new JLabel(
                "<html><center><font size=2 color=gray>Test accounts &mdash; "
                        + "admin / admin123 &nbsp;|&nbsp; reception / reception123</font></center></html>",
                SwingConstants.CENTER);
        testAccountsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        JPanel southPanel = new JPanel();
        southPanel.setBackground(UITheme.BACKGROUND);
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(buttonPanel);
        southPanel.add(testAccountsLabel);
        mainPanel.add(southPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loginButton.addActionListener(this::handleLogin);
        exitButton.addActionListener((ActionEvent e) -> System.exit(0));

        // allow pressing Enter in the password field to submit
        passwordField.addActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // basic input validation before hitting the database
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password are required.");
            return;
        }

        try {
            User loggedInUser = userDAO.validateLogin(username, password);
            statusLabel.setText(" ");
            JOptionPane.showMessageDialog(this,
                    "Welcome, " + loggedInUser.getFullName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            // open the dashboard and close the login window
            new DashboardForm(loggedInUser).setVisible(true);
            this.dispose();

        } catch (InvalidLoginException ex) {
            statusLabel.setText(ex.getMessage());
            passwordField.setText("");
        } catch (SQLException ex) {
            statusLabel.setText("Database error - check your connection.");
            JOptionPane.showMessageDialog(this,
                    "Could not connect to the database:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
