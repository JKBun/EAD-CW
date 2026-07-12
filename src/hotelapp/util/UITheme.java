package hotelapp.util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Small helper class that centralizes the app's color palette and a few
 * reusable styling methods, so every form looks consistent.
 */
public final class UITheme {

    // Palette
    public static final Color PRIMARY = new Color(0x1F4E5F);       // deep teal-blue
    public static final Color PRIMARY_DARK = new Color(0x163944);
    public static final Color ACCENT = new Color(0x2E8B8B);        // teal accent
    public static final Color BACKGROUND = new Color(0xF4F6F7);    // light gray
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TEXT_DARK = new Color(0x2B2B2B);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color DANGER = new Color(0xC0392B);
    public static final Color SUCCESS = new Color(0x27883F);

    private UITheme() {
    }

    /** Styles a JFrame's content pane with the app background color. */
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND);
    }

    /** A bold, colored title banner used at the top of forms. */
    public static JLabel titleBanner(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(PRIMARY);
        label.setForeground(TEXT_LIGHT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        return label;
    }

    /** The main "do the thing" button - filled with the accent color. */
    public static void stylePrimaryButton(JButton button) {
        button.setBackground(ACCENT);
        button.setForeground(TEXT_LIGHT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    /** A softer secondary button (Clear, Refresh, Exit, etc.) */
    public static void styleSecondaryButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(PRIMARY);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
    }

    /** A destructive button (Delete). */
    public static void styleDangerButton(JButton button) {
        button.setBackground(DANGER);
        button.setForeground(TEXT_LIGHT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    /** Colors a JTable's header band to match the theme. */
    public static void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY);
        header.setForeground(TEXT_LIGHT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(24);
        table.setSelectionBackground(ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(0xE0E0E0));
    }

    /** Wraps a titled border with the theme's primary color. */
    public static javax.swing.border.TitledBorder titledBorder(String title) {
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY, 1), title);
        border.setTitleColor(PRIMARY);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        return border;
    }

    /** Converts a Color to a "#RRGGBB" string for use inside HTML button/label text. */
    public static String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}
