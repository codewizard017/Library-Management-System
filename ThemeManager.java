import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class ThemeManager {

    private static boolean isDark = true;

    // 2026 Color Palette
    public static final Color ACCENT_COLOR = new Color(99, 102, 241); // Modern Indigo
    public static final Color ACCENT_HOVER = new Color(79, 70, 229);
    public static final Color SUCCESS_COLOR = new Color(16, 185, 129); // Emerald
    public static final Color DANGER_COLOR = new Color(239, 68, 68);   // Rose / Coral
    public static final Color WARNING_COLOR = new Color(245, 158, 11); // Amber
    public static final Color INFO_COLOR = new Color(59, 130, 246);    // Sky Blue

    // Dark Theme Palette
    public static final Color DARK_BG = new Color(15, 23, 42);          // Slate 900
    public static final Color DARK_SURFACE = new Color(30, 41, 59);     // Slate 800
    public static final Color DARK_CARD = new Color(30, 41, 59);
    public static final Color DARK_BORDER = new Color(51, 65, 85);      // Slate 700
    public static final Color DARK_TEXT_PRIMARY = new Color(248, 250, 252);
    public static final Color DARK_TEXT_MUTED = new Color(148, 163, 184);

    // Light Theme Palette
    public static final Color LIGHT_BG = new Color(241, 245, 249);       // Slate 100
    public static final Color LIGHT_SURFACE = new Color(255, 255, 255);
    public static final Color LIGHT_CARD = new Color(255, 255, 255);
    public static final Color LIGHT_BORDER = new Color(226, 232, 240);   // Slate 200
    public static final Color LIGHT_TEXT_PRIMARY = new Color(15, 23, 42);
    public static final Color LIGHT_TEXT_MUTED = new Color(100, 116, 139);

    public static void initializeTheme() {
        try {
            // Apply Modern FlatLaf configuration
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 10);
            UIManager.put("CheckBox.arc", 6);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 10);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.innerFocusWidth", 0);
            UIManager.put("Table.rowHeight", 36);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("TableHeader.height", 38);

            // Clean modern typography
            Font baseFont = new Font("Segoe UI", Font.PLAIN, 13);
            if (!isFontAvailable("Segoe UI")) {
                baseFont = new Font("SansSerif", Font.PLAIN, 13);
            }
            UIManager.put("defaultFont", baseFont);

            FlatDarkLaf.setup();
            isDark = true;
        } catch (Exception e) {
            System.err.println("Could not load FlatLaf: " + e.getMessage());
        }
    }

    public static boolean isDarkMode() {
        return isDark;
    }

    public static void toggleTheme(Window window) {
        try {
            if (isDark) {
                FlatLightLaf.setup();
                isDark = false;
            } else {
                FlatDarkLaf.setup();
                isDark = true;
            }
            FlatLaf.updateUI();
            if (window != null) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (Exception e) {
            System.err.println("Theme toggle error: " + e.getMessage());
        }
    }

    public static Color getBackgroundColor() {
        return isDark ? DARK_BG : LIGHT_BG;
    }

    public static Color getSurfaceColor() {
        return isDark ? DARK_SURFACE : LIGHT_SURFACE;
    }

    public static Color getBorderColor() {
        return isDark ? DARK_BORDER : LIGHT_BORDER;
    }

    public static Color getTextPrimary() {
        return isDark ? DARK_TEXT_PRIMARY : LIGHT_TEXT_PRIMARY;
    }

    public static Color getTextMuted() {
        return isDark ? DARK_TEXT_MUTED : LIGHT_TEXT_MUTED;
    }

    private static boolean isFontAvailable(String fontName) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (Font font : ge.getAllFonts()) {
            if (font.getFamily().equalsIgnoreCase(fontName)) {
                return true;
            }
        }
        return false;
    }
}

