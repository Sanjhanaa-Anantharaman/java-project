package gui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Shared UI theme constants and custom component factories.
 * Provides rounded, anti-aliased components for a smooth, modern look.
 */
public class Theme {

    // ── Color Palette (Catppuccin Mocha) ───────────────
    public static final Color BG           = new Color(30, 30, 46);
    public static final Color SURFACE_0    = new Color(40, 42, 58);
    public static final Color SURFACE_1    = new Color(49, 50, 68);
    public static final Color SURFACE_2    = new Color(59, 60, 78);
    public static final Color OVERLAY      = new Color(69, 71, 90);
    public static final Color TEXT         = new Color(205, 214, 244);
    public static final Color SUBTEXT      = new Color(147, 153, 178);
    public static final Color ACCENT       = new Color(137, 180, 250);
    public static final Color GREEN        = new Color(166, 227, 161);
    public static final Color RED          = new Color(243, 139, 168);
    public static final Color YELLOW       = new Color(249, 226, 175);
    public static final Color PEACH        = new Color(250, 179, 135);
    public static final Color TEAL         = new Color(148, 226, 213);

    // ── Fonts ──────────────────────────────────────────
    public static final Font TITLE_FONT    = new Font("SansSerif", Font.BOLD, 24);
    public static final Font HEADING_FONT  = new Font("SansSerif", Font.BOLD, 20);
    public static final Font BODY_FONT     = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SMALL_FONT    = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font LABEL_FONT    = new Font("SansSerif", Font.BOLD, 11);
    public static final Font BUTTON_FONT   = new Font("SansSerif", Font.BOLD, 13);
    public static final Font TABLE_FONT    = new Font("SansSerif", Font.PLAIN, 13);

    public static final int ARC = 12; // border-radius

    // ── Enable global anti-aliasing ────────────────────

    public static void applyGlobalSettings() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        UIManager.put("Button.arc", ARC);
        UIManager.put("TextComponent.arc", ARC);
        UIManager.put("OptionPane.background", SURFACE_0);
        UIManager.put("Panel.background", SURFACE_0);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", BODY_FONT);
        UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
        UIManager.put("ComboBox.background", SURFACE_1);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.selectionBackground", ACCENT);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
    }

    // ── Rounded Button ─────────────────────────────────

    public static JButton makeButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            private float hoverAlpha = 0f;
            private Timer hoverTimer;
            {
                setContentAreaFilled(false);
                setOpaque(false);

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        animateHover(true);
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        animateHover(false);
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {
                        hoverAlpha = 0.4f;
                        repaint();
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        hoverAlpha = 0.2f;
                        repaint();
                    }
                });
            }

            private void animateHover(boolean enter) {
                if (hoverTimer != null) hoverTimer.stop();
                float target = enter ? 0.2f : 0f;
                hoverTimer = new Timer(16, ev -> {
                    if (enter) {
                        hoverAlpha = Math.min(hoverAlpha + 0.04f, target);
                    } else {
                        hoverAlpha = Math.max(hoverAlpha - 0.04f, target);
                    }
                    repaint();
                    if (Math.abs(hoverAlpha - target) < 0.01f) {
                        hoverAlpha = target;
                        ((Timer) ev.getSource()).stop();
                    }
                });
                hoverTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Base fill
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));

                // Hover overlay
                if (hoverAlpha > 0) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(hoverAlpha));
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(BUTTON_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        return btn;
    }

    // ── Rounded Text Field ─────────────────────────────

    public static JTextField makeTextField() {
        return makeTextField("");
    }

    public static JTextField makeTextField(String text) {
        JTextField field = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? ACCENT : OVERLAY);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, ARC, ARC));
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setFont(BODY_FONT);
        field.setBackground(SURFACE_1);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.repaint(); }
            public void focusLost(FocusEvent e)   { field.repaint(); }
        });
        return field;
    }

    // ── Rounded Password Field ─────────────────────────

    public static JPasswordField makePasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? ACCENT : OVERLAY);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, ARC, ARC));
                g2.dispose();
            }
        };
        field.setOpaque(false);
        field.setFont(BODY_FONT);
        field.setBackground(SURFACE_1);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { field.repaint(); }
            public void focusLost(FocusEvent e)   { field.repaint(); }
        });
        return field;
    }

    // ── Rounded Card Panel ─────────────────────────────

    public static JPanel makeCard(int padV, int padH) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth() - 2, getHeight() - 2, ARC + 4, ARC + 4));

                // Card
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 3, ARC + 2, ARC + 2));

                // Border
                g2.setColor(OVERLAY);
                g2.setStroke(new BasicStroke(0.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 4, ARC + 2, ARC + 2));

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBackground(SURFACE_0);
        card.setBorder(BorderFactory.createEmptyBorder(padV, padH, padV, padH));
        return card;
    }

    // ── Styled Combo Box ───────────────────────────────

    public static JComboBox<String> makeComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(BODY_FONT);
        combo.setBackground(SURFACE_1);
        combo.setForeground(TEXT);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OVERLAY),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return combo;
    }

    // ── Form Label ─────────────────────────────────────

    public static JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(LABEL_FONT);
        lbl.setForeground(SUBTEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 5, 0));
        return lbl;
    }
}
