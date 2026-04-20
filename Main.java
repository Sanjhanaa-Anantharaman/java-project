import gui.MainFrame;
import gui.Theme;
import javax.swing.*;

/**
 * Entry point – launches the Swing GUI with smooth rendering.
 */
public class Main {
    public static void main(String[] args) {
        // Global anti-aliasing & dark-theme defaults
        Theme.applyGlobalSettings();

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
