package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a student with backlogs (failed subjects).
 * Demonstrates Multilevel Inheritance and Polymorphism (overrides assignGrade).
 * IS-A relationship: BacklogStudent IS-A Student IS-A User.
 */
public class BacklogStudent extends Student {

    private int backlogCount;
    private List<String> backlogSubjects;

    public BacklogStudent(String username, String password, int rollNo, String name, int[] marks) {
        super(username, password, rollNo, name, marks);
        this.backlogSubjects = new ArrayList<>();
        detectBacklogs();
    }

    // ── Getters & Setters ──────────────────────────────

    public int getBacklogCount() {
        return backlogCount;
    }

    public List<String> getBacklogSubjects() {
        return backlogSubjects;
    }

    // ── Backlog Detection ──────────────────────────────

    /**
     * A subject is considered a backlog if marks are below 40 (pass mark).
     */
    private void detectBacklogs() {
        backlogSubjects.clear();
        int[] marks = getMarks();
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] < 40) {
                backlogSubjects.add("Subject " + (i + 1));
            }
        }
        backlogCount = backlogSubjects.size();
    }

    @Override
    public void setMarks(int[] marks) {
        super.setMarks(marks);
        detectBacklogs();
    }

    // ── Polymorphism: grade override ───────────────────

    /**
     * Overrides the parent assignGrade().
     * If the student has any backlog, the grade is "Backlog" regardless of percentage.
     */
    @Override
    public String assignGrade() {
        int[] marks = getMarks();
        for (int m : marks) {
            if (m < 40) {
                return "Backlog";
            }
        }
        return super.assignGrade();
    }

    @Override
    public String getType() {
        return "Backlog";
    }

    // ── Display ────────────────────────────────────────

    @Override
    public void viewResult() {
        super.viewResult();
        if (backlogCount > 0) {
            System.out.println("  ⚠  Backlog Subjects: " + String.join(", ", backlogSubjects));
            System.out.println("  ⚠  Backlog Count   : " + backlogCount);
        }
    }
}
