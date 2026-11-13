package foodordering;

import javax.swing.*;
import java.awt.*;

/**
 * Explicit readable color defaults for controls whose colors may otherwise
 * be inherited from the operating-system Look & Feel (including dark mode).
 */
public final class UITheme {
    private static final Color TEXT = Color.BLACK;
    private static final Color MUTED = new Color(75, 85, 99);
    private static final Color SURFACE = Color.WHITE;
    private static final Color ACCENT = new Color(235, 102, 27);
    private static final Color SELECTED = new Color(255, 239, 228);

    private UITheme() {
    }

    public static void apply() {
        Font body = new Font("SansSerif", Font.PLAIN, 13);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.background", SURFACE);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.background", SURFACE);
        UIManager.put("TextField.caretForeground", TEXT);
        UIManager.put("TextField.selectionBackground", ACCENT);
        UIManager.put("TextField.selectionForeground", Color.WHITE);
        UIManager.put("FormattedTextField.foreground", TEXT);
        UIManager.put("FormattedTextField.background", SURFACE);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.background", SURFACE);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.background", SURFACE);
        UIManager.put("ComboBox.selectionForeground", TEXT);
        UIManager.put("ComboBox.selectionBackground", SELECTED);
        UIManager.put("List.foreground", TEXT);
        UIManager.put("List.background", SURFACE);
        UIManager.put("List.selectionForeground", TEXT);
        UIManager.put("List.selectionBackground", SELECTED);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.background", SURFACE);
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("Table.selectionBackground", SELECTED);
        UIManager.put("TableHeader.foreground", TEXT);
        UIManager.put("TableHeader.background", new Color(244, 246, 249));
        UIManager.put("TextArea.foreground", TEXT);
        UIManager.put("TextArea.background", new Color(250, 251, 252));
        UIManager.put("TextArea.caretForeground", TEXT);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("Panel.background", new Color(246, 247, 249));
        UIManager.put("ToolTip.foreground", TEXT);
        UIManager.put("ToolTip.background", new Color(255, 250, 240));
        UIManager.put("TitledBorder.titleColor", TEXT);
        UIManager.put("Menu.foreground", TEXT);
        UIManager.put("Menu.background", SURFACE);
        UIManager.put("MenuItem.foreground", TEXT);
        UIManager.put("MenuItem.background", SURFACE);
        UIManager.put("MenuBar.foreground", TEXT);
        UIManager.put("MenuBar.background", SURFACE);
        UIManager.put("PopupMenu.foreground", TEXT);
        UIManager.put("PopupMenu.background", SURFACE);
        UIManager.put("CheckBox.foreground", TEXT);
        UIManager.put("CheckBox.background", SURFACE);
        UIManager.put("RadioButton.foreground", TEXT);
        UIManager.put("RadioButton.background", SURFACE);
        UIManager.put("Spinner.foreground", TEXT);
        UIManager.put("Spinner.background", SURFACE);
        UIManager.put("ScrollPane.foreground", TEXT);
        UIManager.put("ScrollPane.background", SURFACE);
        UIManager.put("Viewport.foreground", TEXT);
        UIManager.put("Viewport.background", SURFACE);
        UIManager.put("defaultFont", body);
        UIManager.put("control", SURFACE);
        UIManager.put("text", TEXT);
        UIManager.put("nimbusBase", new Color(25, 38, 64));
        UIManager.put("nimbusFocus", ACCENT);
    }
}
