package gui;

import controller.ResultManager;
import model.BacklogStudent;
import model.Professor;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Professor dashboard with:
 *  - Tabbed pane: All Students | Backlogs | Professors
 *  - Auto-backlog detection (no separate "Add Backlog" button)
 *  - Professor management (add professors with subjects)
 */
public class ProfessorPanel extends JPanel {

    private final ResultManager resultManager;
    private final Professor currentProfessor;
    private final Runnable onLogout;

    // All-students table
    private DefaultTableModel allTableModel;
    private JTable allTable;

    // Backlog table
    private DefaultTableModel backlogTableModel;
    private JTable backlogTable;

    // Professor table
    private DefaultTableModel profTableModel;
    private JTable profTable;

    public ProfessorPanel(ResultManager resultManager, Professor currentProfessor, Runnable onLogout) {
        this.resultManager = resultManager;
        this.currentProfessor = currentProfessor;
        this.onLogout = onLogout;

        setLayout(new BorderLayout(0, 0));
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

        JLabel titleLabel = new JLabel("\uD83D\uDCCA  Prof. " + currentProfessor.getUsername());
        titleLabel.setFont(Theme.HEADING_FONT);
        titleLabel.setForeground(Theme.ACCENT);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setOpaque(false);
        JLabel subjectsLabel = new JLabel("Subjects: " + String.join(", ", currentProfessor.getSubjects()));
        subjectsLabel.setFont(Theme.SMALL_FONT);
        subjectsLabel.setForeground(Theme.SUBTEXT);
        topRight.add(subjectsLabel);
        JButton logoutBtn = Theme.makeButton("Logout", Theme.RED);
        logoutBtn.addActionListener(e -> onLogout.run());
        topRight.add(logoutBtn);
        topBar.add(topRight, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // ── Tabbed pane ────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BUTTON_FONT);
        tabs.setBackground(Theme.SURFACE_0);
        tabs.setForeground(Theme.TEXT);

        tabs.addTab("📋  All Students", buildAllStudentsTab());
        tabs.addTab("⚠️  Backlogs", buildBacklogTab());
        tabs.addTab("👨‍🏫  Professors", buildProfessorTab());

        add(tabs, BorderLayout.CENTER);

        refreshAll();
    }

    // ═══════════════════════════════════════════════════
    //  TAB 1: ALL STUDENTS
    // ═══════════════════════════════════════════════════

    private JPanel buildAllStudentsTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(Theme.BG);

        String[] cols = {"Roll", "Name", "Subject 1", "Subject 2", "Subject 3", "Pct", "Grade", "Type"};
        allTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        allTable = styledTable(allTableModel);

        JScrollPane sp = new JScrollPane(allTable);
        sp.getViewport().setBackground(Theme.BG);
        sp.setBorder(BorderFactory.createEmptyBorder(12, 24, 8, 24));
        tab.add(sp, BorderLayout.CENTER);

        // Buttons
        JPanel bar = buttonBar();
        JButton addBtn     = Theme.makeButton("＋  Add Student", Theme.GREEN);
        JButton updateBtn  = Theme.makeButton("✎  Update Marks", Theme.ACCENT);
        JButton deleteBtn  = Theme.makeButton("✕  Delete", Theme.RED);
        JButton refreshBtn = Theme.makeButton("↻  Refresh", Theme.SUBTEXT);

        addBtn.addActionListener(e -> showAddStudentDialog());
        updateBtn.addActionListener(e -> showUpdateDialog());
        deleteBtn.addActionListener(e -> showDeleteConfirm());
        refreshBtn.addActionListener(e -> refreshAll());

        bar.add(addBtn);
        bar.add(updateBtn);
        bar.add(deleteBtn);
        bar.add(refreshBtn);
        tab.add(bar, BorderLayout.SOUTH);

        return tab;
    }

    // ═══════════════════════════════════════════════════
    //  TAB 2: BACKLOGS
    // ═══════════════════════════════════════════════════

    private JPanel buildBacklogTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(Theme.BG);

        String[] cols = {"Roll", "Name", "Failed Subject", "Marks", "Pct", "Backlog Count"};
        backlogTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        backlogTable = styledTable(backlogTableModel);

        // Color the "Marks" column red
        backlogTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Theme.SURFACE_1 : new Color(44, 45, 62));
                    if (col == 3) { // Marks column
                        c.setForeground(Theme.RED);
                    } else if (col == 2) { // Failed subject
                        c.setForeground(Theme.YELLOW);
                    } else {
                        c.setForeground(Theme.TEXT);
                    }
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(backlogTable);
        sp.getViewport().setBackground(Theme.BG);
        sp.setBorder(BorderFactory.createEmptyBorder(12, 24, 8, 24));
        tab.add(sp, BorderLayout.CENTER);

        // Info label at bottom
        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
        info.setBackground(Theme.SURFACE_0);
        JLabel infoLabel = new JLabel("Students with marks below 30 in any subject are automatically listed here.");
        infoLabel.setFont(Theme.SMALL_FONT);
        infoLabel.setForeground(Theme.SUBTEXT);
        info.add(infoLabel);
        tab.add(info, BorderLayout.SOUTH);

        return tab;
    }

    // ═══════════════════════════════════════════════════
    //  TAB 3: PROFESSORS
    // ═══════════════════════════════════════════════════

    private JPanel buildProfessorTab() {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setBackground(Theme.BG);

        String[] cols = {"Username", "Subjects"};
        profTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        profTable = styledTable(profTableModel);

        JScrollPane sp = new JScrollPane(profTable);
        sp.getViewport().setBackground(Theme.BG);
        sp.setBorder(BorderFactory.createEmptyBorder(12, 24, 8, 24));
        tab.add(sp, BorderLayout.CENTER);

        JPanel bar = buttonBar();
        JButton addBtn = Theme.makeButton("＋  Add Professor", Theme.GREEN);
        JButton delBtn = Theme.makeButton("✕  Remove", Theme.RED);

        addBtn.addActionListener(e -> showAddProfessorDialog());
        delBtn.addActionListener(e -> showDeleteProfessorConfirm());

        bar.add(addBtn);
        bar.add(delBtn);
        tab.add(bar, BorderLayout.SOUTH);

        return tab;
    }

    // ═══════════════════════════════════════════════════
    //  REFRESH
    // ═══════════════════════════════════════════════════

    private void refreshAll() {
        refreshAllStudents();
        refreshBacklogs();
        refreshProfessors();
    }

    private void refreshAllStudents() {
        allTableModel.setRowCount(0);
        for (Student s : resultManager.getStudents()) {
            int[] m = s.getMarks();
            String[] sn = s.getSubjectNames();
            allTableModel.addRow(new Object[]{
                    s.getRollNo(), s.getName(),
                    sn[0] + ": " + m[0],
                    sn[1] + ": " + m[1],
                    sn[2] + ": " + m[2],
                    String.format("%.1f%%", s.getPercentage()),
                    s.getGrade(), s.getType()
            });
        }
    }

    private void refreshBacklogs() {
        backlogTableModel.setRowCount(0);
        for (BacklogStudent bs : resultManager.getBacklogStudents()) {
            int[] marks = bs.getMarks();
            String[] subs = bs.getSubjectNames();
            for (int i = 0; i < marks.length; i++) {
                if (marks[i] < BacklogStudent.PASS_MARK) {
                    backlogTableModel.addRow(new Object[]{
                            bs.getRollNo(), bs.getName(),
                            subs[i], marks[i],
                            String.format("%.1f%%", bs.getPercentage()),
                            bs.getBacklogCount()
                    });
                }
            }
        }
    }

    private void refreshProfessors() {
        profTableModel.setRowCount(0);
        for (Professor p : resultManager.getProfessors()) {
            profTableModel.addRow(new Object[]{
                    p.getUsername(),
                    String.join(", ", p.getSubjects())
            });
        }
    }

    // ═══════════════════════════════════════════════════
    //  DIALOGS
    // ═══════════════════════════════════════════════════

    private void showAddStudentDialog() {
        JPanel form = darkForm();
        JTextField rollField = addRow(form, "Roll Number");
        JTextField nameField = addRow(form, "Name");

        // Subject names from the current professor's subjects (or custom)
        List<String> subs = currentProfessor.getSubjects();
        String subjectHint = subs.isEmpty() ? "e.g. Maths" : subs.size() >= 3
                ? subs.get(0) + ", " + subs.get(1) + ", " + subs.get(2) : String.join(", ", subs);

        JTextField s1Name = addRow(form, "Subject 1 Name");
        JTextField s1Mark = addRow(form, "Subject 1 Marks");
        JTextField s2Name = addRow(form, "Subject 2 Name");
        JTextField s2Mark = addRow(form, "Subject 2 Marks");
        JTextField s3Name = addRow(form, "Subject 3 Name");
        JTextField s3Mark = addRow(form, "Subject 3 Marks");

        // Pre-fill subject names from professor's subjects
        if (subs.size() >= 1) s1Name.setText(subs.get(0));
        if (subs.size() >= 2) s2Name.setText(subs.get(1));
        if (subs.size() >= 3) s3Name.setText(subs.get(2));

        int result = JOptionPane.showConfirmDialog(this, form, "Add Student",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int roll = Integer.parseInt(rollField.getText().trim());
                String name = nameField.getText().trim();
                if (name.isEmpty()) { showError("Name cannot be empty."); return; }

                String[] subNames = {
                    s1Name.getText().trim().isEmpty() ? "Subject 1" : s1Name.getText().trim(),
                    s2Name.getText().trim().isEmpty() ? "Subject 2" : s2Name.getText().trim(),
                    s3Name.getText().trim().isEmpty() ? "Subject 3" : s3Name.getText().trim()
                };
                int[] marks = {
                    Integer.parseInt(s1Mark.getText().trim()),
                    Integer.parseInt(s2Mark.getText().trim()),
                    Integer.parseInt(s3Mark.getText().trim())
                };

                Student added = resultManager.addStudent(roll, name, subNames, marks);
                if (added != null) {
                    refreshAll();
                    String msg = "Student added!\nLogin: student" + roll + " / pass" + roll;
                    if (added instanceof BacklogStudent) {
                        msg += "\n\n⚠ Auto-detected as BACKLOG (marks < 30).";
                    }
                    showInfo(msg);
                } else {
                    showError("Roll number " + roll + " already exists.");
                }
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for roll and marks.");
            }
        }
    }

    private void showUpdateDialog() {
        int row = allTable.getSelectedRow();
        if (row < 0) { showError("Select a student row first."); return; }

        int rollNo = (int) allTableModel.getValueAt(row, 0);
        Student student = resultManager.findByRollNo(rollNo);
        if (student == null) return;

        JPanel form = darkForm();
        JTextField m1 = addRow(form, student.getSubjectNames()[0]);
        JTextField m2 = addRow(form, student.getSubjectNames()[1]);
        JTextField m3 = addRow(form, student.getSubjectNames()[2]);
        m1.setText(String.valueOf(student.getMarks()[0]));
        m2.setText(String.valueOf(student.getMarks()[1]));
        m3.setText(String.valueOf(student.getMarks()[2]));

        int result = JOptionPane.showConfirmDialog(this, form,
                "Update Marks — " + student.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int[] marks = {
                    Integer.parseInt(m1.getText().trim()),
                    Integer.parseInt(m2.getText().trim()),
                    Integer.parseInt(m3.getText().trim())
                };
                resultManager.updateMarks(rollNo, marks);
                refreshAll();
                String msg = "Marks updated for " + student.getName();
                if (BacklogStudent.hasBacklogs(marks)) {
                    msg += "\n⚠ Student now has backlog(s).";
                }
                showInfo(msg);
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers.");
            }
        }
    }

    private void showDeleteConfirm() {
        int row = allTable.getSelectedRow();
        if (row < 0) { showError("Select a student row first."); return; }

        int rollNo = (int) allTableModel.getValueAt(row, 0);
        String name = (String) allTableModel.getValueAt(row, 1);

        int result = JOptionPane.showConfirmDialog(this,
                "Delete student \"" + name + "\" (Roll: " + rollNo + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            resultManager.deleteStudent(rollNo);
            refreshAll();
        }
    }

    private void showAddProfessorDialog() {
        JPanel form = darkForm();
        JTextField userField = addRow(form, "Username");
        JTextField passField = addRow(form, "Password");
        JTextField subsField = addRow(form, "Subjects (comma-separated)");
        subsField.setText("Maths, Physics, Chemistry");

        int result = JOptionPane.showConfirmDialog(this, form, "Add Professor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String user = userField.getText().trim();
            String pass = passField.getText().trim();
            if (user.isEmpty() || pass.isEmpty()) {
                showError("Username and password are required.");
                return;
            }

            List<String> subs = new ArrayList<>();
            for (String s : subsField.getText().split(",")) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) subs.add(trimmed);
            }

            Professor prof = new Professor(user, pass, subs);
            if (resultManager.addProfessor(prof)) {
                refreshProfessors();
                showInfo("Professor '" + user + "' created!\nLogin: " + user + " / " + pass);
            } else {
                showError("Username '" + user + "' already exists.");
            }
        }
    }

    private void showDeleteProfessorConfirm() {
        int row = profTable.getSelectedRow();
        if (row < 0) { showError("Select a professor row first."); return; }

        String username = (String) profTableModel.getValueAt(row, 0);

        if (username.equals(currentProfessor.getUsername())) {
            showError("You cannot remove yourself.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Remove professor \"" + username + "\"?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            resultManager.deleteProfessor(username);
            refreshProfessors();
        }
    }

    // ═══════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════

    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(Theme.TABLE_FONT);
        table.setRowHeight(36);
        table.setBackground(Theme.SURFACE_1);
        table.setForeground(Theme.TEXT);
        table.setSelectionBackground(new Color(Theme.ACCENT.getRed(), Theme.ACCENT.getGreen(), Theme.ACCENT.getBlue(), 80));
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(69, 71, 90, 60));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Theme.SURFACE_1 : new Color(44, 45, 62));
                    c.setForeground(Theme.TEXT);

                    // Grade color
                    if (val != null) {
                        String v = val.toString();
                        if (v.equals("Backlog")) c.setForeground(Theme.RED);
                        else if (v.equals("A+") || v.equals("A")) c.setForeground(Theme.GREEN);
                    }
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(Theme.SURFACE_2);
        header.setForeground(Theme.ACCENT);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));

        return table;
    }

    private JPanel buttonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 16)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Theme.SURFACE_0);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bar.setOpaque(false);
        return bar;
    }

    private JPanel darkForm() {
        JPanel form = new JPanel(new GridLayout(0, 2, 14, 12));
        form.setBackground(Theme.SURFACE_0);
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return form;
    }

    private JTextField addRow(JPanel form, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(Theme.TEXT);
        lbl.setFont(Theme.BODY_FONT);
        form.add(lbl);
        JTextField f = Theme.makeTextField();
        form.add(f);
        return f;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
