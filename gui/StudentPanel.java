package gui;

import model.BacklogStudent;
import model.Student;

import javax.swing.*;
import java.awt.*;

/**
 * Read-only result card displayed to a logged-in student.
 * Now shows subject names.
 */
public class StudentPanel extends JPanel {

    public StudentPanel(Student student, Runnable onLogout) {
        setLayout(new BorderLayout());
        setBackground(Theme.BG);

        // ── Top bar ────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.SURFACE_0);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Theme.ACCENT);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        JLabel titleLabel = new JLabel("\uD83C\uDF93  Welcome, " + student.getName());
        titleLabel.setFont(Theme.HEADING_FONT);
        titleLabel.setForeground(Theme.ACCENT);
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton logoutBtn = Theme.makeButton("Logout", Theme.RED);
        logoutBtn.addActionListener(e -> onLogout.run());
        topBar.add(logoutBtn, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ── Result Card ────────────────────────────────
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.BG);

        JPanel card = Theme.makeCard(34, 44);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 450));

        JLabel header = new JLabel("\uD83D\uDCCB  Result Card");
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setForeground(Theme.ACCENT);
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(header);
        card.add(Box.createVerticalStrut(8));
        card.add(makeDivider());
        card.add(Box.createVerticalStrut(20));

        addRow(card, "Roll No", String.valueOf(student.getRollNo()), Theme.TEXT);
        addRow(card, "Name", student.getName(), Theme.TEXT);

        int[] marks = student.getMarks();
        String[] subs = student.getSubjectNames();
        for (int i = 0; i < marks.length; i++) {
            Color mColor = marks[i] < BacklogStudent.PASS_MARK ? Theme.RED : Theme.TEXT;
            addRow(card, subs[i], String.valueOf(marks[i]), mColor);
        }

        card.add(Box.createVerticalStrut(6));
        card.add(makeDivider());
        card.add(Box.createVerticalStrut(6));

        addRow(card, "Percentage", String.format("%.1f%%", student.getPercentage()), Theme.ACCENT);
        addRow(card, "Grade", student.getGrade(), gradeColor(student.getGrade()));
        addRow(card, "Type", student.getType(),
               student.getType().equals("Backlog") ? Theme.YELLOW : Theme.GREEN);

        if (student instanceof BacklogStudent) {
            BacklogStudent bs = (BacklogStudent) student;
            if (bs.getBacklogCount() > 0) {
                card.add(Box.createVerticalStrut(14));
                JPanel warnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                warnPanel.setOpaque(false);
                warnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
                JLabel warn = new JLabel("⚠  Backlogs: " + String.join(", ", bs.getBacklogSubjects()));
                warn.setFont(new Font("SansSerif", Font.BOLD, 13));
                warn.setForeground(Theme.RED);
                warnPanel.add(warn);
                card.add(warnPanel);
            }
        }

        center.add(card);
        add(center, BorderLayout.CENTER);
    }

    private void addRow(JPanel card, String label, String value, Color valueColor) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(Theme.SUBTEXT);
        row.add(lbl, BorderLayout.WEST);
        JLabel val = new JLabel(value);
        val.setFont(Theme.BODY_FONT);
        val.setForeground(valueColor);
        row.add(val, BorderLayout.EAST);
        card.add(row);
    }

    private JPanel makeDivider() {
        JPanel div = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0,
                        new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(), Theme.ACCENT.getBlue(), 0),
                        getWidth() / 2f, 0,
                        new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(), Theme.ACCENT.getBlue(), 80), true));
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        div.setOpaque(false);
        div.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        div.setPreferredSize(new Dimension(0, 1));
        return div;
    }

    private Color gradeColor(String grade) {
        switch (grade) {
            case "A+": case "A": return Theme.GREEN;
            case "B":  return Theme.TEAL;
            case "C":  return Theme.YELLOW;
            case "D": case "E": return Theme.PEACH;
            case "F": case "Backlog": return Theme.RED;
            default: return Theme.TEXT;
        }
    }
}
