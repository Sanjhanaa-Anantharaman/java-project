package model;

/**
 * Represents a regular student with marks and result computation.
 * Demonstrates Inheritance (extends User) and Encapsulation.
 */
public class Student extends User {

    private int rollNo;
    private String name;
    private int[] marks;          // marks for 3 subjects
    private double percentage;
    private String grade;

    public Student(String username, String password, int rollNo, String name, int[] marks) {
        super(username, password);
        this.rollNo = rollNo;
        this.name = name;
        this.marks = marks;
        this.percentage = calculatePercentage();
        this.grade = assignGrade();
    }

    // ── Getters & Setters ──────────────────────────────

    public int getRollNo() {
        return rollNo;
    }

    public void setRollNo(int rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getMarks() {
        return marks;
    }

    public void setMarks(int[] marks) {
        this.marks = marks;
        this.percentage = calculatePercentage();
        this.grade = assignGrade();
    }

    public double getPercentage() {
        return percentage;
    }

    public String getGrade() {
        return grade;
    }

    // ── Result Calculation ─────────────────────────────

    public double calculatePercentage() {
        if (marks == null || marks.length == 0) return 0;
        int total = 0;
        for (int m : marks) {
            total += m;
        }
        return Math.round((total * 100.0) / (marks.length * 100));
    }

    /**
     * Assigns grade based on percentage.
     * This method is overridden in BacklogStudent (Polymorphism).
     */
    public String assignGrade() {
        double pct = calculatePercentage();
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B";
        if (pct >= 60) return "C";
        if (pct >= 50) return "D";
        if (pct >= 40) return "E";
        return "F";
    }

    /**
     * Returns the student type string used in CSV serialization.
     */
    public String getType() {
        return "Normal";
    }

    // ── Display ────────────────────────────────────────

    public void viewResult() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         STUDENT RESULT CARD          ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Roll No    : %-22d ║%n", rollNo);
        System.out.printf("║  Name       : %-22s ║%n", name);
        for (int i = 0; i < marks.length; i++) {
            System.out.printf("║  Subject %d  : %-22d ║%n", i + 1, marks[i]);
        }
        System.out.printf("║  Percentage : %-22.1f ║%n", percentage);
        System.out.printf("║  Grade      : %-22s ║%n", grade);
        System.out.printf("║  Type       : %-22s ║%n", getType());
        System.out.println("╚══════════════════════════════════════╝");
    }

    /**
     * Converts this student to a CSV line for file storage.
     */
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(rollNo).append(",");
        sb.append(name).append(",");
        for (int m : marks) {
            sb.append(m).append(",");
        }
        sb.append(String.format("%.1f", percentage)).append(",");
        sb.append(grade).append(",");
        sb.append(getType()).append(",");
        sb.append(getUsername()).append(",");
        sb.append(getPassword());
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%-6d %-15s %4d %4d %4d   %5.1f%%   %-5s  %s",
                rollNo, name, marks[0], marks[1], marks[2], percentage, grade, getType());
    }
}
