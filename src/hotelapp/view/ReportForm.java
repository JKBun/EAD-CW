package hotelapp.view;

import hotelapp.dao.BookingDAO;
import hotelapp.model.Booking;
import hotelapp.util.UITheme;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reports screen - generates the JasperReports "Bookings Report", which
 * pulls data from three tables (bookings, customers, rooms) via BookingDAO.
 */
public class ReportForm extends JFrame {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final String JRXML_RESOURCE = "/reports/BookingsReport.jrxml";

    private final BookingDAO bookingDAO = new BookingDAO();

    public ReportForm() {
        setTitle("Reports");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 220);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        UITheme.styleFrame(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel infoLabel = new JLabel(
                "<html>Generates a report of all bookings, joining data<br>"
                        + "from Bookings, Customers, and Rooms tables,<br>"
                        + "with total revenue and booking count.</html>");
        panel.add(infoLabel, BorderLayout.CENTER);

        JButton generateBtn = new JButton("Generate Bookings Report");
        UITheme.stylePrimaryButton(generateBtn);
        generateBtn.addActionListener(e -> generateReport());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(UITheme.BACKGROUND);
        buttonPanel.add(generateBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void generateReport() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();

            if (bookings.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No bookings yet - create a booking first.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Convert Booking objects into the flat Map rows the .jrxml expects
            List<Map<String, ?>> rows = new ArrayList<>();
            for (Booking b : bookings) {
                Map<String, Object> row = new HashMap<>();
                row.put("bookingId", b.getBookingId());
                row.put("customerName", b.getCustomerName());
                row.put("roomNumber", b.getRoomNumber());
                row.put("checkIn", b.getCheckInDate().format(DATE_FMT));
                row.put("checkOut", b.getCheckOutDate().format(DATE_FMT));
                row.put("totalAmount", b.getTotalAmount());
                row.put("status", b.getStatus());
                rows.add(row);
            }

            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(rows);

            try (InputStream jrxmlStream = getClass().getResourceAsStream(JRXML_RESOURCE)) {
                if (jrxmlStream == null) {
                    throw new JRException("Could not find " + JRXML_RESOURCE
                            + " on the classpath. Make sure the 'reports' folder is inside src/.");
                }
                JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(
                        jasperReport, new HashMap<>(), dataSource);

                JasperViewer.viewReport(jasperPrint, false);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (JRException ex) {
            JOptionPane.showMessageDialog(this,
                    "Report generation error:\n" + ex.getMessage(),
                    "Report Error", JOptionPane.ERROR_MESSAGE);
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not read the report template file:\n" + ex.getMessage(),
                    "Report Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
