package hotelapp.view;

import hotelapp.model.User;
import hotelapp.util.UITheme;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard - shown after a successful login.
 * Acts as the navigation hub to the other forms in the system.
 */
public class DashboardForm extends JFrame {

    private final User currentUser;

    public DashboardForm(User currentUser) {
        this.currentUser = currentUser;

        setTitle("Hotel Management System - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 420);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        UITheme.styleFrame(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND);

        JLabel welcomeLabel = UITheme.titleBanner(
                "Welcome, " + currentUser.getFullName() + "  (" + currentUser.getRole() + ")");
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        gridPanel.setBackground(UITheme.BACKGROUND);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton bookingBtn = makeNavButton("New Booking", "Create a room booking for a guest");
        JButton roomsBtn = makeNavButton("Manage Rooms", "Add, edit or remove rooms");
        JButton customersBtn = makeNavButton("Manage Customers", "Add, edit or remove guest records");
        JButton reportsBtn = makeNavButton("Reports", "View booking & revenue reports");

        bookingBtn.addActionListener(e -> new BookingForm(currentUser).setVisible(true));
        roomsBtn.addActionListener(e -> new RoomManagementForm().setVisible(true));
        customersBtn.addActionListener(e -> new CustomerManagementForm().setVisible(true));
        reportsBtn.addActionListener(e -> new ReportForm().setVisible(true));

        gridPanel.add(bookingBtn);
        gridPanel.add(roomsBtn);
        gridPanel.add(customersBtn);
        gridPanel.add(reportsBtn);

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Log Out");
        UITheme.styleSecondaryButton(logoutBtn);
        logoutBtn.addActionListener(e -> {
            new LoginForm().setVisible(true);
            this.dispose();
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UITheme.BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 20));
        bottomPanel.add(logoutBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton makeNavButton(String title, String subtitle) {
        String mainColor = UITheme.toHex(UITheme.TEXT_LIGHT);
        String subColor = "#E0F0F0"; // pale teal, readable on the accent button background
        JButton button = new JButton(
                "<html><center><b><font color='" + mainColor + "'>" + title + "</font></b><br>"
                        + "<font size=2 color='" + subColor + "'>" + subtitle + "</font></center></html>");
        button.setPreferredSize(new Dimension(200, 90));
        UITheme.stylePrimaryButton(button);
        return button;
    }
}
