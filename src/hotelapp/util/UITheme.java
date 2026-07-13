package hotelapp.util;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UITheme {

    
    public static final Color PRIMARY = new Color(0x1F4E5F);       
    public static final Color PRIMARY_DARK = new Color(0x163944);
    public static final Color ACCENT = new Color(0x2E8B8B);        
    public static final Color BACKGROUND = new Color(0xF4F6F7);  
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TEXT_DARK = new Color(0x2B2B2B);
    public static final Color TEXT_LIGHT = Color.WHITE;
    public static final Color DANGER = new Color(0xC0392B);
    public static final Color SUCCESS = new Color(0x27883F);

    private UITheme() {
    }

    
    public static void styleFrame(JFrame frame) {
        frame.getContentPane().setBackground(BACKGROUND);
    }

    public static JLabel titleBanner(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(PRIMARY);
        label.setForeground(TEXT_LIGHT);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setBorder(BorderFactory.createEmptyBorder(14, 10, 14, 10));
        return label;
    }

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

    public static javax.swing.border.TitledBorder titledBorder(String title) {
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY, 1), title);
        border.setTitleColor(PRIMARY);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        return border;
    }

    public static String toHex(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
}
