package storage;

import model.BacklogStudent;
import model.Professor;
import model.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * File-based persistence for students and professors.
 *
 * Student CSV: rollNo,name,sub1;sub2;sub3,m1;m2;m3,percentage,grade,type,username,password
 * Professor CSV: username,password,sub1;sub2;sub3
 */
public class FileHandler {

    private static final String DATA_DIR      = "data";
    private static final String STUDENT_FILE  = DATA_DIR + "/students.csv";
    private static final String PROFESSOR_FILE = DATA_DIR + "/professors.csv";

    private static final String STUDENT_HEADER  = "rollNo,name,subjects,marks,percentage,grade,type,username,password";
    private static final String PROFESSOR_HEADER = "username,password,subjects";

    // ── Ensure data dir ────────────────────────────────

    private static void ensureDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();
    }

    // ═══════════════════════════════════════════════════
    //  STUDENTS
    // ═══════════════════════════════════════════════════

    public static void saveStudents(ArrayList<Student> students) {
        ensureDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(STUDENT_FILE))) {
            pw.println(STUDENT_HEADER);
            for (Student s : students) pw.println(s.toCSV());
            System.out.println("✅ Student data saved.");
        } catch (IOException e) {
            System.out.println("❌ Error saving students: " + e.getMessage());
        }
    }

    public static ArrayList<Student> loadStudents() {
        ArrayList<Student> students = new ArrayList<>();
        File file = new File(STUDENT_FILE);
        if (!file.exists()) {
            System.out.println("ℹ  No student data file found. Starting fresh.");
            return students;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 9) continue;

                int rollNo      = Integer.parseInt(parts[0].trim());
                String name     = parts[1].trim();
                String[] subs   = parts[2].trim().split(";");
                String[] mStrs  = parts[3].trim().split(";");
                // parts[4]=pct, parts[5]=grade → recalculated
                String type     = parts[6].trim();
                String username = parts[7].trim();
                String password = parts[8].trim();

                int[] marks = new int[mStrs.length];
                for (int i = 0; i < mStrs.length; i++) {
                    marks[i] = Integer.parseInt(mStrs[i].trim());
                }

                Student student;
                if (type.equalsIgnoreCase("Backlog")) {
                    student = new BacklogStudent(username, password, rollNo, name, subs, marks);
                } else {
                    student = new Student(username, password, rollNo, name, subs, marks);
                }
                students.add(student);
            }
            System.out.println("✅ Loaded " + students.size() + " student(s).");
        } catch (Exception e) {
            System.out.println("❌ Error loading students: " + e.getMessage());
        }
        return students;
    }

    // ═══════════════════════════════════════════════════
    //  PROFESSORS
    // ═══════════════════════════════════════════════════

    public static void saveProfessors(ArrayList<Professor> professors) {
        ensureDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PROFESSOR_FILE))) {
            pw.println(PROFESSOR_HEADER);
            for (Professor p : professors) pw.println(p.toCSV());
            System.out.println("✅ Professor data saved.");
        } catch (IOException e) {
            System.out.println("❌ Error saving professors: " + e.getMessage());
        }
    }

    public static ArrayList<Professor> loadProfessors() {
        ArrayList<Professor> professors = new ArrayList<>();

        File file = new File(PROFESSOR_FILE);
        if (!file.exists()) {
            // Seed with default admin professor
            List<String> defaultSubs = Arrays.asList("Maths", "Physics", "Chemistry");
            professors.add(new Professor("admin", "admin123", defaultSubs));
            saveProfessors(professors);
            System.out.println("ℹ  Created default professor: admin / admin123");
            return professors;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String username = parts[0].trim();
                String password = parts[1].trim();
                List<String> subs = new ArrayList<>();
                if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                    for (String s : parts[2].trim().split(";")) {
                        subs.add(s.trim());
                    }
                }
                professors.add(new Professor(username, password, subs));
            }
            System.out.println("✅ Loaded " + professors.size() + " professor(s).");
        } catch (Exception e) {
            System.out.println("❌ Error loading professors: " + e.getMessage());
        }

        if (professors.isEmpty()) {
            List<String> defaultSubs = Arrays.asList("Maths", "Physics", "Chemistry");
            professors.add(new Professor("admin", "admin123", defaultSubs));
            saveProfessors(professors);
        }

        return professors;
    }
}
