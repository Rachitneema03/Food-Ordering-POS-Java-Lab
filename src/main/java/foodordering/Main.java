package foodordering;

import javax.swing.*;

public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                UITheme.apply();
                Database.connect().close();
                new POSFrame().setVisible(true);
            } catch (Exception e) {
                UIManager.put("OptionPane.messageForeground", new java.awt.Color(31, 41, 55));
                JOptionPane.showMessageDialog(null, "Startup failed: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
