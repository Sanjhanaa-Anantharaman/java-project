package controller;

import model.BacklogStudent;
import model.Student;
import storage.FileHandler;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Central controller for managing student records.
 * Uses an ArrayList<Student> as the in-memory data store.
 */
public class ResultManager {

    private ArrayList<Student> students;

    public ResultManager() {
        this.students = FileHandler.loadFromFile();
    }

    // ── CRUD Operations ────────────────────────────────

    /**
     * Adds a new student. Rejects duplicates by roll number.
     */
    public boolean addStudent(Student student) {
        if (findByRollNo(student.getRollNo()) != null) {
            System.out.println("❌ A student with roll number " + student.getRollNo() + " already exists.");
            return false;
        }
        students.add(student);
        save();
        System.out.println("✅ Student '" + student.getName() + "' added successfully.");
        return true;
    }

    /**
     * Updates marks for an existing student.
     */
    public boolean updateMarks(int rollNo, int[] newMarks) {
        Student student = findByRollNo(rollNo);
        if (student == null) {
            System.out.println("❌ Student with roll number " + rollNo + " not found.");
            return false;
        }
        student.setMarks(newMarks);
        save();
        System.out.println("✅ Marks updated for student: " + student.getName());
        return true;
    }

    /**
     * Deletes a student by roll number.
     */
    public boolean deleteStudent(int rollNo) {
        Student student = findByRollNo(rollNo);
        if (student == null) {
            System.out.println("❌ Student with roll number " + rollNo + " not found.");
            return false;
        }
        students.remove(student);
        save();
        System.out.println("✅ Student '" + student.getName() + "' deleted.");
        return true;
    }

    // ── Search & View ──────────────────────────────────

    /**
     * Finds a student by roll number.
     */
    public Student findByRollNo(int rollNo) {
        for (Student s : students) {
            if (s.getRollNo() == rollNo) return s;
        }
        return null;
    }

    /**
     * Finds a student by their login credentials.
     */
    public Student findByCredentials(String username, String password) {
        for (Student s : students) {
            if (s.login(username, password)) return s;
        }
        return null;
    }

    /**
     * Displays all student records in a tabular format.
     */
    public void viewAllStudents() {
        if (students.isEmpty()) {
            System.out.println("ℹ  No student records found.");
            return;
        }

        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                          ALL STUDENT RECORDS                              ║");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ %-6s %-15s %4s %4s %4s   %6s   %-5s  %-8s ║%n",
                "Roll", "Name", "S1", "S2", "S3", "Pct", "Grade", "Type");
        System.out.println("╠════════════════════════════════════════════════════════════════════════════╣");
        for (Student s : students) {
            System.out.printf("║ %s ║%n", s.toString());
        }
        System.out.println("╚════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("Total records: " + students.size());
    }

    // ── Sorting ────────────────────────────────────────

    /**
     * Sorts students by percentage in descending order.
     */
    public void sortByPercentageDesc() {
        students.sort(Comparator.comparingDouble(Student::getPercentage).reversed());
        System.out.println("✅ Students sorted by percentage (highest first).");
    }

    /**
     * Sorts students by roll number in ascending order.
     */
    public void sortByRollNo() {
        students.sort(Comparator.comparingInt(Student::getRollNo));
        System.out.println("✅ Students sorted by roll number.");
    }

    // ── Persistence ────────────────────────────────────

    private void save() {
        FileHandler.saveToFile(students);
    }

    public ArrayList<Student> getStudents() {
        return students;
    }
}
