import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class UI {
    public static void main(String[] args) {
        Theme.applyGlobalSettings();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

class Theme {
    static final Color BG        = new Color(30, 30, 46);
    static final Color SURFACE_0 = new Color(40, 42, 58);
    static final Color SURFACE_1 = new Color(49, 50, 68);
    static final Color SURFACE_2 = new Color(59, 60, 78);
    static final Color OVERLAY   = new Color(69, 71, 90);
    static final Color TEXT      = new Color(205, 214, 244);
    static final Color SUBTEXT   = new Color(147, 153, 178);
    static final Color ACCENT    = new Color(137, 180, 250);
    static final Color GREEN     = new Color(166, 227, 161);
    static final Color RED       = new Color(243, 139, 168);
    static final Color YELLOW    = new Color(249, 226, 175);
    static final Color PEACH     = new Color(250, 179, 135);
    static final Color TEAL      = new Color(148, 226, 213);

    static final Font TITLE_FONT  = new Font("SansSerif", Font.BOLD, 24);
    static final Font HEADING_FONT= new Font("SansSerif", Font.BOLD, 20);
    static final Font BODY_FONT   = new Font("SansSerif", Font.PLAIN, 14);
    static final Font SMALL_FONT  = new Font("SansSerif", Font.PLAIN, 12);
    static final Font LABEL_FONT  = new Font("SansSerif", Font.BOLD, 11);
    static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 13);
    static final Font TABLE_FONT  = new Font("SansSerif", Font.PLAIN, 13);
    static final int  ARC = 12;

    static void applyGlobalSettings() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        UIManager.put("OptionPane.background", SURFACE_0);
        UIManager.put("Panel.background", SURFACE_0);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", BODY_FONT);
        UIManager.put("OptionPane.buttonFont", BUTTON_FONT);
    }

    static JButton makeButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            float hoverAlpha = 0f;
            Timer hoverTimer;
            {
                setContentAreaFilled(false);
                setOpaque(false);
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e)  { animate(true); }
                    public void mouseExited(MouseEvent e)   { animate(false); }
                    public void mousePressed(MouseEvent e)  { hoverAlpha = 0.4f; repaint(); }
                    public void mouseReleased(MouseEvent e) { hoverAlpha = 0.2f; repaint(); }
                });
            }
            void animate(boolean in) {
                if (hoverTimer != null) hoverTimer.stop();
                float target = in ? 0.2f : 0f;
                hoverTimer = new Timer(16, ev -> {
                    hoverAlpha += in ? 0.04f : -0.04f;
                    hoverAlpha = Math.max(0, Math.min(hoverAlpha, 0.2f));
                    repaint();
                    if (Math.abs(hoverAlpha - target) < 0.01f) { hoverAlpha = target; ((Timer)ev.getSource()).stop(); }
                });
                hoverTimer.start();
            }
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                if (hoverAlpha > 0) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(hoverAlpha));
                    g2.setColor(Color.WHITE);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(BUTTON_FONT); btn.setForeground(Color.WHITE); btn.setBackground(baseColor);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        return btn;
    }

    static JTextField makeTextField() { return makeTextField(""); }
    static JTextField makeTextField(String text) {
        JTextField f = new JTextField(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                g2.dispose(); super.paintComponent(g);
            }
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? ACCENT : OVERLAY);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(.5f,.5f,getWidth()-1,getHeight()-1,ARC,ARC));
                g2.dispose();
            }
        };
        f.setOpaque(false); f.setFont(BODY_FONT); f.setBackground(SURFACE_1);
        f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.repaint(); }
            public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    static JPasswordField makePasswordField() {
        JPasswordField f = new JPasswordField() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                g2.dispose(); super.paintComponent(g);
            }
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? ACCENT : OVERLAY);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1f));
                g2.draw(new RoundRectangle2D.Float(.5f,.5f,getWidth()-1,getHeight()-1,ARC,ARC));
                g2.dispose();
            }
        };
        f.setOpaque(false); f.setFont(BODY_FONT); f.setBackground(SURFACE_1);
        f.setForeground(TEXT); f.setCaretColor(TEXT);
        f.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.repaint(); }
            public void focusLost(FocusEvent e)   { f.repaint(); }
        });
        return f;
    }

    static JPanel makeCard(int padV, int padH) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new RoundRectangle2D.Float(2, 3, getWidth()-2, getHeight()-2, ARC+4, ARC+4));
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-2, getHeight()-3, ARC+2, ARC+2));
                g2.setColor(OVERLAY); g2.setStroke(new BasicStroke(0.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth()-3, getHeight()-4, ARC+2, ARC+2));
                g2.dispose();
            }
        };
        card.setOpaque(false); card.setBackground(SURFACE_0);
        card.setBorder(BorderFactory.createEmptyBorder(padV, padH, padV, padH));
        return card;
    }

    static JComboBox<String> makeComboBox(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(BODY_FONT); c.setBackground(SURFACE_1); c.setForeground(TEXT);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(OVERLAY),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return c;
    }

    static JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT); l.setForeground(SUBTEXT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 4, 5, 0));
        return l;
    }
}

class LoginPanel extends JPanel {
    interface LoginListener { void onLogin(String role, String username, String password); }

    private final JTextField      usernameField;
    private final JPasswordField  passwordField;
    private final JComboBox<String> roleCombo;
    private final JLabel          statusLabel;

    LoginPanel(LoginListener listener) {
        setLayout(new GridBagLayout());
        setBackground(Theme.BG);

        JPanel card = Theme.makeCard(44, 52);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(430, 500));
        card.setMaximumSize(new Dimension(430, 500));

        JLabel title = new JLabel("Student Result Manager");
        title.setFont(Theme.TITLE_FONT); title.setForeground(Theme.ACCENT);
        title.setAlignmentX(CENTER_ALIGNMENT); card.add(title);

        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(Theme.SMALL_FONT); sub.setForeground(Theme.SUBTEXT);
        sub.setAlignmentX(CENTER_ALIGNMENT); card.add(sub);
        card.add(Box.createVerticalStrut(32));

        card.add(Theme.makeLabel("ROLE"));
        roleCombo = Theme.makeComboBox(new String[]{"Professor", "Student"});
        card.add(roleCombo); card.add(Box.createVerticalStrut(18));

        card.add(Theme.makeLabel("USERNAME"));
        usernameField = Theme.makeTextField();
        card.add(usernameField); card.add(Box.createVerticalStrut(18));

        card.add(Theme.makeLabel("PASSWORD"));
        passwordField = Theme.makePasswordField();
        card.add(passwordField); card.add(Box.createVerticalStrut(28));

        JButton loginBtn = Theme.makeButton("Login", Theme.ACCENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        loginBtn.setAlignmentX(CENTER_ALIGNMENT);
        card.add(loginBtn); card.add(Box.createVerticalStrut(16));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(Theme.SMALL_FONT); statusLabel.setForeground(Theme.RED);
        statusLabel.setAlignmentX(CENTER_ALIGNMENT); card.add(statusLabel);
        add(card);

        loginBtn.addActionListener(e -> {
            String r = (String) roleCombo.getSelectedItem();
            String u = usernameField.getText().trim();
            String p = new String(passwordField.getPassword()).trim();
            if (u.isEmpty() || p.isEmpty()) { statusLabel.setText("Please fill in all fields."); return; }
            listener.onLogin(r, u, p);
        });
        passwordField.addActionListener(e -> loginBtn.doClick());
    }

    void setStatus(String s) { statusLabel.setText(s); }
    void clearFields()       { usernameField.setText(""); passwordField.setText(""); statusLabel.setText(" "); }
}

class ProfessorPanel extends JPanel {
    private final ResultManager  rm;
    private final Professor      prof;
    private DefaultTableModel    allModel, backlogModel, profModel;
    private JTable               allTable, backlogTable, profTable;

    ProfessorPanel(ResultManager rm, Professor prof, Runnable onLogout) {
        this.rm   = rm;
        this.prof = prof;
        setLayout(new BorderLayout()); setBackground(Theme.BG);

        // Top bar
        JPanel top = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setColor(Theme.SURFACE_0); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Theme.ACCENT);    g2.fillRect(0,getHeight()-2,getWidth(),2);
                g2.dispose();
            }
        };
        top.setOpaque(false); top.setBorder(BorderFactory.createEmptyBorder(16,28,16,28));
        JLabel t = new JLabel("\uD83D\uDCCA  Prof. " + prof.getUsername());
        t.setFont(Theme.HEADING_FONT); t.setForeground(Theme.ACCENT);
        top.add(t, BorderLayout.WEST);
        JPanel tr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); tr.setOpaque(false);
        JLabel sl = new JLabel("Subjects: " + String.join(", ", prof.getSubjects()));
        sl.setFont(Theme.SMALL_FONT); sl.setForeground(Theme.SUBTEXT); tr.add(sl);
        JButton lb = Theme.makeButton("Logout", Theme.RED);
        lb.addActionListener(e -> onLogout.run()); tr.add(lb);
        top.add(tr, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.BUTTON_FONT); tabs.setBackground(Theme.SURFACE_0); tabs.setForeground(Theme.TEXT);
        tabs.addTab("\uD83D\uDCCB  All Students", buildAllTab());
        tabs.addTab("⚠  Backlogs", buildBacklogTab());
        tabs.addTab("\uD83D\uDC68\u200D\uD83C\uDFEB  Professors", buildProfTab());
        add(tabs, BorderLayout.CENTER);
        refreshAll();
    }

    private JPanel buildAllTab() {
        JPanel tab = new JPanel(new BorderLayout()); tab.setBackground(Theme.BG);
        String[] c = {"Roll","Name","Subject 1","Subject 2","Subject 3","Pct","Grade","Type"};
        allModel = new DefaultTableModel(c,0){ public boolean isCellEditable(int r,int co){return false;} };
        allTable = styledTable(allModel);
        JScrollPane sp = new JScrollPane(allTable); sp.getViewport().setBackground(Theme.BG);
        sp.setBorder(BorderFactory.createEmptyBorder(12,24,8,24)); tab.add(sp, BorderLayout.CENTER);

        JPanel bar = buttonBar();
        JButton a = Theme.makeButton("＋  Add Student", Theme.GREEN);
        JButton u = Theme.makeButton("✎  Update Marks", Theme.ACCENT);
        JButton d = Theme.makeButton("✕  Delete", Theme.RED);
        JButton r = Theme.makeButton("↻  Refresh", Theme.SUBTEXT);
        a.addActionListener(e -> showAddStudent()); u.addActionListener(e -> showUpdate());
        d.addActionListener(e -> showDelete());     r.addActionListener(e -> refreshAll());
        bar.add(a); bar.add(u); bar.add(d); bar.add(r);
        tab.add(bar, BorderLayout.SOUTH);
        return tab;
    }

    private JPanel buildBacklogTab() {
        JPanel tab = new JPanel(new BorderLayout()); tab.setBackground(Theme.BG);
        String[] c = {"Roll","Name","Failed Subject","Marks","Pct","Backlog Count"};
        backlogModel = new DefaultTableModel(c,0){ public boolean isCellEditable(int r,int co){return false;} };
        backlogTable = styledTable(backlogModel);
        backlogTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int row,int col) {
                Component c2 = super.getTableCellRendererComponent(t,v,s,f,row,col);
                if (!s) {
                    c2.setBackground(row%2==0 ? Theme.SURFACE_1 : new Color(44,45,62));
                    c2.setForeground(col==3 ? Theme.RED : col==2 ? Theme.YELLOW : Theme.TEXT);
                }
                ((JLabel)c2).setBorder(BorderFactory.createEmptyBorder(0,12,0,12));
                return c2;
            }
        });
        JScrollPane sp = new JScrollPane(backlogTable); sp.getViewport().setBackground(Theme.BG);
        sp.setBorder(BorderFactory.createEmptyBorder(12,24,8,24)); tab.add(sp, BorderLayout.CENTER);
        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT,24,12)); info.setBackground(Theme.SURFACE_0);
        JLabel il = new JLabel("Students with marks below 30 in any subject appear here automatically.");
        il.setFont(Theme.SMALL_FONT); il.setForeground(Theme.SUBTEXT); info.add(il);
        tab.add(info, BorderLayout.SOUTH);
        return tab;
    }

    private JPanel buildProfTab() {
        JPanel tab = new JPanel(new BorderLayout()); tab.setBackground(Theme.BG);
        String[] c = {"Username","Subjects"};
        profModel = new DefaultTableModel(c,0){ public boolean isCellEditable(int r,int co){return false;} };
        profTable = styledTable(profModel);
        JScrollPane sp = new JScrollPane(profTable); sp.getViewport().setBackground(Theme.BG);
        sp.setBorder(BorderFactory.createEmptyBorder(12,24,8,24)); tab.add(sp, BorderLayout.CENTER);
        JPanel bar = buttonBar();
        JButton a = Theme.makeButton("＋  Add Professor", Theme.GREEN);
        JButton d = Theme.makeButton("✕  Remove", Theme.RED);
        a.addActionListener(e -> showAddProf()); d.addActionListener(e -> showDelProf());
        bar.add(a); bar.add(d);
        tab.add(bar, BorderLayout.SOUTH);
        return tab;
    }

    void refreshAll() {
        allModel.setRowCount(0);
        for (Student s : rm.getStudents()) {
            int[] m = s.getMarks(); String[] sn = s.getSubjectNames();
            allModel.addRow(new Object[]{s.getRollNo(), s.getName(),
                    sn[0]+": "+m[0], sn[1]+": "+m[1], sn[2]+": "+m[2],
                    String.format("%.1f%%",s.getPercentage()), s.getGrade(), s.getType()});
        }
        backlogModel.setRowCount(0);
        for (BacklogStudent bs : rm.getBacklogStudents()) {
            int[] m = bs.getMarks(); String[] sn = bs.getSubjectNames();
            for (int i = 0; i < m.length; i++)
                if (m[i] < BacklogStudent.PASS_MARK)
                    backlogModel.addRow(new Object[]{bs.getRollNo(), bs.getName(), sn[i], m[i],
                            String.format("%.1f%%",bs.getPercentage()), bs.getBacklogCount()});
        }
        profModel.setRowCount(0);
        for (Professor p : rm.getProfessors())
            profModel.addRow(new Object[]{p.getUsername(), String.join(", ", p.getSubjects())});
    }

    private void showAddStudent() {
        JPanel f = darkForm();
        JTextField roll = addRow(f,"Roll Number"), name = addRow(f,"Name");
        JTextField s1n = addRow(f,"Subject 1 Name"), s1m = addRow(f,"Subject 1 Marks");
        JTextField s2n = addRow(f,"Subject 2 Name"), s2m = addRow(f,"Subject 2 Marks");
        JTextField s3n = addRow(f,"Subject 3 Name"), s3m = addRow(f,"Subject 3 Marks");
        List<String> ps = prof.getSubjects();
        if (ps.size()>=1) s1n.setText(ps.get(0));
        if (ps.size()>=2) s2n.setText(ps.get(1));
        if (ps.size()>=3) s3n.setText(ps.get(2));
        if (JOptionPane.showConfirmDialog(this,f,"Add Student",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION) {
            try {
                int r = Integer.parseInt(roll.getText().trim());
                String n = name.getText().trim();
                if (n.isEmpty()) { err("Name is required."); return; }
                String[] subs = { s1n.getText().trim().isEmpty()?"Subject 1":s1n.getText().trim(),
                        s2n.getText().trim().isEmpty()?"Subject 2":s2n.getText().trim(),
                        s3n.getText().trim().isEmpty()?"Subject 3":s3n.getText().trim() };
                int[] marks = { Integer.parseInt(s1m.getText().trim()),
                        Integer.parseInt(s2m.getText().trim()), Integer.parseInt(s3m.getText().trim()) };
                Student added = rm.addStudent(r, n, subs, marks);
                if (added != null) {
                    refreshAll();
                    String msg = "Student added!\nLogin: student"+r+" / pass"+r;
                    if (added instanceof BacklogStudent) msg += "\n\n⚠ Auto-detected as BACKLOG (marks < 30).";
                    info(msg);
                } else err("Roll number already exists.");
            } catch (NumberFormatException ex) { err("Enter valid numbers."); }
        }
    }

    private void showUpdate() {
        int row = allTable.getSelectedRow();
        if (row < 0) { err("Select a student row first."); return; }
        int rollNo = (int) allModel.getValueAt(row, 0);
        Student s = rm.findByRollNo(rollNo);
        if (s == null) return;
        JPanel f = darkForm();
        JTextField m1 = addRow(f, s.getSubjectNames()[0]);
        JTextField m2 = addRow(f, s.getSubjectNames()[1]);
        JTextField m3 = addRow(f, s.getSubjectNames()[2]);
        m1.setText(""+s.getMarks()[0]); m2.setText(""+s.getMarks()[1]); m3.setText(""+s.getMarks()[2]);
        if (JOptionPane.showConfirmDialog(this,f,"Update — "+s.getName(),JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION) {
            try {
                int[] marks = {Integer.parseInt(m1.getText().trim()),Integer.parseInt(m2.getText().trim()),Integer.parseInt(m3.getText().trim())};
                rm.updateMarks(rollNo, marks); refreshAll();
                String msg = "Marks updated.";
                if (BacklogStudent.hasBacklogs(marks)) msg += "\n⚠ Student now has backlog(s).";
                info(msg);
            } catch (NumberFormatException ex) { err("Enter valid numbers."); }
        }
    }

    private void showDelete() {
        int row = allTable.getSelectedRow();
        if (row < 0) { err("Select a student row first."); return; }
        int rollNo = (int) allModel.getValueAt(row, 0);
        String name = (String) allModel.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this,"Delete \""+name+"\" (Roll: "+rollNo+")?",
                "Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
            rm.deleteStudent(rollNo); refreshAll();
        }
    }

    private void showAddProf() {
        JPanel f = darkForm();
        JTextField u = addRow(f,"Username"), p = addRow(f,"Password"), s = addRow(f,"Subjects (comma-sep)");
        s.setText("Maths, Physics, Chemistry");
        if (JOptionPane.showConfirmDialog(this,f,"Add Professor",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION) {
            String un=u.getText().trim(), pw=p.getText().trim();
            if (un.isEmpty()||pw.isEmpty()) { err("Username and password required."); return; }
            List<String> subs = new ArrayList<>();
            for (String x : s.getText().split(",")) { String t=x.trim(); if (!t.isEmpty()) subs.add(t); }
            if (rm.addProfessor(new Professor(un,pw,subs))) { refreshAll(); info("Professor '"+un+"' created!\nLogin: "+un+" / "+pw); }
            else err("Username already exists.");
        }
    }

    private void showDelProf() {
        int row = profTable.getSelectedRow();
        if (row < 0) { err("Select a professor row first."); return; }
        String un = (String) profModel.getValueAt(row, 0);
        if (un.equals(prof.getUsername())) { err("Cannot remove yourself."); return; }
        if (JOptionPane.showConfirmDialog(this,"Remove \""+un+"\"?","Confirm",
                JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
            rm.deleteProfessor(un); refreshAll();
        }
    }

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(Theme.TABLE_FONT); t.setRowHeight(36);
        t.setBackground(Theme.SURFACE_1); t.setForeground(Theme.TEXT);
        t.setSelectionBackground(new Color(Theme.ACCENT.getRed(),Theme.ACCENT.getGreen(),Theme.ACCENT.getBlue(),80));
        t.setSelectionForeground(Color.WHITE);
        t.setGridColor(new Color(69,71,90,60)); t.setShowHorizontalLines(true); t.setShowVerticalLines(false);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable ta,Object v,boolean s,boolean f,int row,int col) {
                Component c = super.getTableCellRendererComponent(ta,v,s,f,row,col);
                if (!s) {
                    c.setBackground(row%2==0?Theme.SURFACE_1:new Color(44,45,62));
                    c.setForeground(Theme.TEXT);
                    if(v!=null){ String vs=v.toString();
                        if(vs.equals("Backlog"))c.setForeground(Theme.RED);
                        else if(vs.equals("A+")||vs.equals("A"))c.setForeground(Theme.GREEN); }
                }
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,12,0,12));
                return c;
            }
        });
        JTableHeader h = t.getTableHeader();
        h.setBackground(Theme.SURFACE_2); h.setForeground(Theme.ACCENT);
        h.setFont(new Font("SansSerif",Font.BOLD,13));
        h.setPreferredSize(new Dimension(h.getPreferredSize().width,40));
        return t;
    }

    private JPanel buttonBar() {
        JPanel b = new JPanel(new FlowLayout(FlowLayout.CENTER,14,16)){
            protected void paintComponent(Graphics g){ g.setColor(Theme.SURFACE_0); g.fillRect(0,0,getWidth(),getHeight()); }
        };
        b.setOpaque(false); return b;
    }

    private JPanel darkForm() {
        JPanel f = new JPanel(new GridLayout(0,2,14,12));
        f.setBackground(Theme.SURFACE_0); f.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        return f;
    }

    private JTextField addRow(JPanel form, String label) {
        JLabel l = new JLabel(label); l.setForeground(Theme.TEXT); l.setFont(Theme.BODY_FONT); form.add(l);
        JTextField f = Theme.makeTextField(); form.add(f); return f;
    }

    private void err(String m)  { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private void info(String m) { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
}

class StudentPanel extends JPanel {
    StudentPanel(Student student, Runnable onLogout) {
        setLayout(new BorderLayout()); setBackground(Theme.BG);

        // Top bar
        JPanel top = new JPanel(new BorderLayout()) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setColor(Theme.SURFACE_0); g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(Theme.ACCENT); g2.fillRect(0,getHeight()-2,getWidth(),2);
                g2.dispose();
            }
        };
        top.setOpaque(false); top.setBorder(BorderFactory.createEmptyBorder(16,28,16,28));
        JLabel tl = new JLabel("\uD83C\uDF93  Welcome, " + student.getName());
        tl.setFont(Theme.HEADING_FONT); tl.setForeground(Theme.ACCENT); top.add(tl, BorderLayout.WEST);
        JButton lb = Theme.makeButton("Logout", Theme.RED);
        lb.addActionListener(e -> onLogout.run()); top.add(lb, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Card
        JPanel center = new JPanel(new GridBagLayout()); center.setBackground(Theme.BG);
        JPanel card = Theme.makeCard(34, 44);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(460, 450));

        JLabel h = new JLabel("\uD83D\uDCCB  Result Card");
        h.setFont(new Font("SansSerif",Font.BOLD,22)); h.setForeground(Theme.ACCENT);
        h.setAlignmentX(CENTER_ALIGNMENT); card.add(h);
        card.add(Box.createVerticalStrut(8)); card.add(divider()); card.add(Box.createVerticalStrut(20));

        row(card, "Roll No", ""+student.getRollNo(), Theme.TEXT);
        row(card, "Name",    student.getName(),      Theme.TEXT);

        int[] marks = student.getMarks(); String[] subs = student.getSubjectNames();
        for (int i=0; i<marks.length; i++)
            row(card, subs[i], ""+marks[i], marks[i]<BacklogStudent.PASS_MARK ? Theme.RED : Theme.TEXT);

        card.add(Box.createVerticalStrut(6)); card.add(divider()); card.add(Box.createVerticalStrut(6));
        row(card, "Percentage", String.format("%.1f%%", student.getPercentage()), Theme.ACCENT);
        row(card, "Grade", student.getGrade(), gradeColor(student.getGrade()));
        row(card, "Type", student.getType(), student.getType().equals("Backlog")?Theme.YELLOW:Theme.GREEN);

        if (student instanceof BacklogStudent) {
            BacklogStudent bs = (BacklogStudent) student;
            if (bs.getBacklogCount() > 0) {
                card.add(Box.createVerticalStrut(14));
                JPanel wp = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0)); wp.setOpaque(false);
                wp.setMaximumSize(new Dimension(Integer.MAX_VALUE,24));
                JLabel w = new JLabel("⚠  Backlogs: " + String.join(", ", bs.getBacklogSubjects()));
                w.setFont(new Font("SansSerif",Font.BOLD,13)); w.setForeground(Theme.RED);
                wp.add(w); card.add(wp);
            }
        }
        center.add(card); add(center, BorderLayout.CENTER);
    }

    private void row(JPanel card, String label, String value, Color vc) {
        JPanel r = new JPanel(new BorderLayout()); r.setOpaque(false);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        r.setBorder(BorderFactory.createEmptyBorder(3,4,3,4));
        JLabel l = new JLabel(label); l.setFont(new Font("SansSerif",Font.BOLD,13)); l.setForeground(Theme.SUBTEXT); r.add(l,BorderLayout.WEST);
        JLabel v = new JLabel(value); v.setFont(Theme.BODY_FONT); v.setForeground(vc); r.add(v,BorderLayout.EAST);
        card.add(r);
    }

    private JPanel divider() {
        JPanel d = new JPanel(){
            protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setPaint(new GradientPaint(0,0,new Color(Theme.ACCENT.getRed(),Theme.ACCENT.getGreen(),Theme.ACCENT.getBlue(),0),
                        getWidth()/2f,0,new Color(Theme.ACCENT.getRed(),Theme.ACCENT.getGreen(),Theme.ACCENT.getBlue(),80),true));
                g2.fillRect(0,0,getWidth(),1); g2.dispose();
            }
        };
        d.setOpaque(false); d.setMaximumSize(new Dimension(Integer.MAX_VALUE,1));
        d.setPreferredSize(new Dimension(0,1)); return d;
    }

    private Color gradeColor(String g) {
        switch(g) {
            case "A+": case "A": return Theme.GREEN;
            case "B": return Theme.TEAL;
            case "C": return Theme.YELLOW;
            case "D": case "E": return Theme.PEACH;
            case "F": case "Backlog": return Theme.RED;
            default: return Theme.TEXT;
        }
    }
}

class MainFrame extends JFrame {
    private final CardLayout  cardLayout = new CardLayout();
    private final JPanel      mainPanel  = new JPanel(cardLayout);
    private final ResultManager rm;
    private final LoginPanel  loginPanel;

    MainFrame() {
        super("Student Result Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(960, 640);
        setMinimumSize(new Dimension(800, 540));
        setLocationRelativeTo(null);

        rm = new ResultManager();
        mainPanel.setBackground(Theme.BG);
        loginPanel = new LoginPanel(this::handleLogin);
        mainPanel.add(loginPanel, "login");
        setContentPane(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    private void handleLogin(String role, String u, String p) {
        if (role.equals("Professor")) {
            Professor pr = rm.findProfessor(u, p);
            if (pr != null) { showProf(pr); } else { loginPanel.setStatus("Invalid professor credentials."); }
        } else {
            Student s = rm.findByCredentials(u, p);
            if (s != null) { showStudent(s); } else { loginPanel.setStatus("Invalid student credentials."); }
        }
    }

    private void showProf(Professor p) {
        remove(ProfessorPanel.class);
        mainPanel.add(new ProfessorPanel(rm, p, this::showLogin), "prof");
        cardLayout.show(mainPanel, "prof");
    }

    private void showStudent(Student s) {
        remove(StudentPanel.class);
        mainPanel.add(new StudentPanel(s, this::showLogin), "student");
        cardLayout.show(mainPanel, "student");
    }

    private void showLogin() { loginPanel.clearFields(); cardLayout.show(mainPanel, "login"); }

    private void remove(Class<?> type) {
        for (Component c : mainPanel.getComponents()) if (type.isInstance(c)) mainPanel.remove(c);
    }
}
