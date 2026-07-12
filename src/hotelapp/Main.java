package hotelapp;

import hotelapp.view.LoginForm;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Run the UI on the Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginForm().setVisible(true);
        });
    }
}
