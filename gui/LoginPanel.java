package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Login panel with role selection and credential input.
 * Uses Theme for smooth, rounded, anti-aliased components.
 */
public class LoginPanel extends JPanel {

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JComboBox<String> roleCombo;
    private final JButton loginButton;
    private final JLabel statusLabel;

    public LoginPanel(LoginListener listener) {
        setLayout(new GridBagLayout());
        setBackground(Theme.BG);

        // ── Card ───────────────────────────────────────
        JPanel card = Theme.makeCard(44, 52);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(430, 500));
        card.setMaximumSize(new Dimension(430, 500));

        // Title
        JLabel title = new JLabel("Student Result Manager");
        title.setFont(Theme.TITLE_FONT);
        title.setForeground(Theme.ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);

        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setFont(Theme.SMALL_FONT);
        subtitle.setForeground(Theme.SUBTEXT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(32));

        // Role
        card.add(Theme.makeLabel("ROLE"));
        roleCombo = Theme.makeComboBox(new String[]{"Professor", "Student"});
        card.add(roleCombo);
        card.add(Box.createVerticalStrut(18));

        // Username
        card.add(Theme.makeLabel("USERNAME"));
        usernameField = Theme.makeTextField();
        card.add(usernameField);
        card.add(Box.createVerticalStrut(18));

        // Password
        card.add(Theme.makeLabel("PASSWORD"));
        passwordField = Theme.makePasswordField();
        card.add(passwordField);
        card.add(Box.createVerticalStrut(28));

        // Login button
        loginButton = Theme.makeButton("Login", Theme.ACCENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(loginButton);
        card.add(Box.createVerticalStrut(16));

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.SMALL_FONT);
        statusLabel.setForeground(Theme.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);

        add(card);

        // ── Actions ────────────────────────────────────
        loginButton.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();
            if (user.isEmpty() || pass.isEmpty()) {
                statusLabel.setText("Please fill in all fields.");
                return;
            }
            listener.onLogin(role, user, pass);
        });

        passwordField.addActionListener(e -> loginButton.doClick());
    }

    public void setStatus(String msg) { statusLabel.setText(msg); }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }

    public interface LoginListener {
        void onLogin(String role, String username, String password);
    }
}
