package gui;

import controller.ResultManager;
import model.Professor;
import model.Student;

import javax.swing.*;
import java.awt.*;

/**
 * Root JFrame using CardLayout to switch between Login, Professor, and Student views.
 * Now supports multiple professor accounts.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final ResultManager resultManager;

    private LoginPanel loginPanel;

    private static final String LOGIN_CARD     = "login";
    private static final String PROFESSOR_CARD = "professor";
    private static final String STUDENT_CARD   = "student";

    public MainFrame() {
        super("Student Result Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setMinimumSize(new Dimension(800, 540));
        setLocationRelativeTo(null);

        resultManager = new ResultManager();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Theme.BG);

        loginPanel = new LoginPanel(this::handleLogin);
        mainPanel.add(loginPanel, LOGIN_CARD);

        setContentPane(mainPanel);
        cardLayout.show(mainPanel, LOGIN_CARD);
    }

    private void handleLogin(String role, String username, String password) {
        if (role.equals("Professor")) {
            Professor prof = resultManager.findProfessor(username, password);
            if (prof != null) {
                showProfessorView(prof);
            } else {
                loginPanel.setStatus("Invalid professor credentials.");
            }
        } else {
            Student student = resultManager.findByCredentials(username, password);
            if (student != null) {
                showStudentView(student);
            } else {
                loginPanel.setStatus("Invalid student credentials.");
            }
        }
    }

    private void showProfessorView(Professor prof) {
        removeCards(ProfessorPanel.class);
        ProfessorPanel panel = new ProfessorPanel(resultManager, prof, this::showLogin);
        mainPanel.add(panel, PROFESSOR_CARD);
        cardLayout.show(mainPanel, PROFESSOR_CARD);
    }

    private void showStudentView(Student student) {
        removeCards(StudentPanel.class);
        StudentPanel panel = new StudentPanel(student, this::showLogin);
        mainPanel.add(panel, STUDENT_CARD);
        cardLayout.show(mainPanel, STUDENT_CARD);
    }

    private void showLogin() {
        loginPanel.clearFields();
        cardLayout.show(mainPanel, LOGIN_CARD);
    }

    private void removeCards(Class<?> type) {
        for (Component c : mainPanel.getComponents()) {
            if (type.isInstance(c)) mainPanel.remove(c);
        }
    }
}
