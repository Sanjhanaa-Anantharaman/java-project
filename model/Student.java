package model;

/**
 * Represents a regular student with marks and result computation.
 * Subjects now have names (chosen when adding the student).
 */
public class Student extends User {

    private int rollNo;
    private String name;
    private String[] subjectNames;  // e.g. {"Maths", "Physics", "Chemistry"}
    private int[] marks;
    private double percentage;
    private String grade;

    public Student(String username, String password, int rollNo, String name,
                   String[] subjectNames, int[] marks) {
        super(username, password);
        this.rollNo = rollNo;
        this.name = name;
        this.subjectNames = subjectNames;
        this.marks = marks;
        this.percentage = calculatePercentage();
        this.grade = assignGrade();
    }

    // ── Getters & Setters ──────────────────────────────

    public int getRollNo() { return rollNo; }
    public void setRollNo(int rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String[] getSubjectNames() { return subjectNames; }
    public void setSubjectNames(String[] subjectNames) { this.subjectNames = subjectNames; }

    public int[] getMarks() { return marks; }
    public void setMarks(int[] marks) {
        this.marks = marks;
        this.percentage = calculatePercentage();
        this.grade = assignGrade();
    }

    public double getPercentage() { return percentage; }
    public String getGrade() { return grade; }

    // ── Result Calculation ─────────────────────────────

    public double calculatePercentage() {
        if (marks == null || marks.length == 0) return 0;
        int total = 0;
        for (int m : marks) total += m;
        return Math.round((total * 100.0) / (marks.length * 100));
    }

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

    public String getType() { return "Normal"; }

    // ── Display ────────────────────────────────────────

    public void viewResult() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         STUDENT RESULT CARD          ║");
        System.out.println("╠══════════════════════════════════════╣");
        System.out.printf("║  Roll No    : %-22d ║%n", rollNo);
        System.out.printf("║  Name       : %-22s ║%n", name);
        for (int i = 0; i < marks.length; i++) {
            System.out.printf("║  %-10s : %-22d ║%n", subjectNames[i], marks[i]);
        }
        System.out.printf("║  Percentage : %-22.1f ║%n", percentage);
        System.out.printf("║  Grade      : %-22s ║%n", grade);
        System.out.println("╚══════════════════════════════════════╝");
    }

    /**
     * CSV: rollNo,name,sub1;sub2;sub3,m1;m2;m3,percentage,grade,type,username,password
     */
    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append(rollNo).append(",");
        sb.append(name).append(",");
        sb.append(String.join(";", subjectNames)).append(",");
        StringBuilder ms = new StringBuilder();
        for (int i = 0; i < marks.length; i++) {
            if (i > 0) ms.append(";");
            ms.append(marks[i]);
        }
        sb.append(ms).append(",");
        sb.append(String.format("%.1f", percentage)).append(",");
        sb.append(grade).append(",");
        sb.append(getType()).append(",");
        sb.append(getUsername()).append(",");
        sb.append(getPassword());
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6d %-15s", rollNo, name));
        for (int m : marks) sb.append(String.format(" %4d", m));
        sb.append(String.format("   %5.1f%%   %-7s  %s", percentage, grade, getType()));
        return sb.toString();
    }
}
