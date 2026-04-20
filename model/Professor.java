package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a professor who can manage student records.
 * Professors have subjects they teach and can be added by other professors.
 */
public class Professor extends User {

    private List<String> subjects;

    public Professor(String username, String password, List<String> subjects) {
        super(username, password);
        this.subjects = (subjects != null) ? subjects : new ArrayList<>();
    }

    public Professor(String username, String password) {
        this(username, password, new ArrayList<>());
    }

    // ── Getters & Setters ──────────────────────────────

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    /**
     * CSV format: username,password,sub1;sub2;sub3
     */
    public String toCSV() {
        return getUsername() + "," + getPassword() + "," + String.join(";", subjects);
    }

    @Override
    public String toString() {
        return "Professor{username='" + getUsername() + "', subjects=" + subjects + "}";
    }
}
