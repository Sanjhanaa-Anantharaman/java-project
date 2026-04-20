package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student with backlogs (failed subjects — marks below 30).
 * Demonstrates Multilevel Inheritance and Polymorphism (overrides assignGrade).
 */
public class BacklogStudent extends Student {

    public static final int PASS_MARK = 30;

    private int backlogCount;
    private List<String> backlogSubjects;

    public BacklogStudent(String username, String password, int rollNo, String name,
                          String[] subjectNames, int[] marks) {
        super(username, password, rollNo, name, subjectNames, marks);
        this.backlogSubjects = new ArrayList<>();
        detectBacklogs();
    }

    // ── Getters ────────────────────────────────────────

    public int getBacklogCount() {
        return backlogCount;
    }

    public List<String> getBacklogSubjects() {
        return backlogSubjects;
    }

    // ── Backlog Detection ──────────────────────────────

    private void detectBacklogs() {
        backlogSubjects.clear();
        int[] marks = getMarks();
        String[] names = getSubjectNames();
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] < PASS_MARK) {
                backlogSubjects.add(names[i]);
            }
        }
        backlogCount = backlogSubjects.size();
    }

    @Override
    public void setMarks(int[] marks) {
        super.setMarks(marks);
        detectBacklogs();
    }

    // ── Polymorphism ───────────────────────────────────

    @Override
    public String assignGrade() {
        int[] marks = getMarks();
        for (int m : marks) {
            if (m < PASS_MARK) return "Backlog";
        }
        return super.assignGrade();
    }

    @Override
    public String getType() {
        return "Backlog";
    }

    @Override
    public void viewResult() {
        super.viewResult();
        if (backlogCount > 0) {
            System.out.println("  ⚠  Backlog Subjects: " + String.join(", ", backlogSubjects));
            System.out.println("  ⚠  Backlog Count   : " + backlogCount);
        }
    }

    /**
     * Checks if any mark is below the pass threshold.
     */
    public static boolean hasBacklogs(int[] marks) {
        for (int m : marks) {
            if (m < PASS_MARK) return true;
        }
        return false;
    }
}
