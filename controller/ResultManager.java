package controller;

import model.BacklogStudent;
import model.Professor;
import model.Student;
import storage.FileHandler;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Central controller for managing student and professor records.
 */
public class ResultManager {

    private ArrayList<Student> students;
    private ArrayList<Professor> professors;

    public ResultManager() {
        this.students = FileHandler.loadStudents();
        this.professors = FileHandler.loadProfessors();
    }

    // ═══════════════════════════════════════════════════
    //  STUDENT CRUD
    // ═══════════════════════════════════════════════════

    /**
     * Adds a student. Automatically creates a BacklogStudent if any mark < 30.
     * Returns the added student (may be upgraded to BacklogStudent).
     */
    public Student addStudent(int rollNo, String name, String[] subjectNames, int[] marks) {
        if (findByRollNo(rollNo) != null) {
            System.out.println("❌ Roll number " + rollNo + " already exists.");
            return null;
        }

        String username = "student" + rollNo;
        String password = "pass" + rollNo;

        Student student;
        if (BacklogStudent.hasBacklogs(marks)) {
            student = new BacklogStudent(username, password, rollNo, name, subjectNames, marks);
        } else {
            student = new Student(username, password, rollNo, name, subjectNames, marks);
        }

        students.add(student);
        saveStudents();
        System.out.println("✅ Student '" + name + "' added.");
        return student;
    }

    /**
     * Updates marks. If the new marks push the student into backlog territory,
     * replaces the Student object with a BacklogStudent (and vice-versa).
     */
    public boolean updateMarks(int rollNo, int[] newMarks) {
        Student old = findByRollNo(rollNo);
        if (old == null) {
            System.out.println("❌ Roll number " + rollNo + " not found.");
            return false;
        }

        boolean needsBacklog = BacklogStudent.hasBacklogs(newMarks);
        boolean isBacklog = old instanceof BacklogStudent;

        if (needsBacklog == isBacklog) {
            // Same type — just update marks
            old.setMarks(newMarks);
        } else {
            // Type changed — replace object
            students.remove(old);
            Student replacement;
            if (needsBacklog) {
                replacement = new BacklogStudent(old.getUsername(), old.getPassword(),
                        old.getRollNo(), old.getName(), old.getSubjectNames(), newMarks);
            } else {
                replacement = new Student(old.getUsername(), old.getPassword(),
                        old.getRollNo(), old.getName(), old.getSubjectNames(), newMarks);
            }
            students.add(replacement);
        }

        saveStudents();
        System.out.println("✅ Marks updated for " + old.getName());
        return true;
    }

    public boolean deleteStudent(int rollNo) {
        Student student = findByRollNo(rollNo);
        if (student == null) {
            System.out.println("❌ Roll number " + rollNo + " not found.");
            return false;
        }
        students.remove(student);
        saveStudents();
        System.out.println("✅ Student '" + student.getName() + "' deleted.");
        return true;
    }

    // ── Search ─────────────────────────────────────────

    public Student findByRollNo(int rollNo) {
        for (Student s : students) {
            if (s.getRollNo() == rollNo) return s;
        }
        return null;
    }

    public Student findByCredentials(String username, String password) {
        for (Student s : students) {
            if (s.login(username, password)) return s;
        }
        return null;
    }

    // ── Backlog filter ─────────────────────────────────

    public ArrayList<BacklogStudent> getBacklogStudents() {
        ArrayList<BacklogStudent> list = new ArrayList<>();
        for (Student s : students) {
            if (s instanceof BacklogStudent) {
                list.add((BacklogStudent) s);
            }
        }
        return list;
    }

    // ── Sort ───────────────────────────────────────────

    public void sortByPercentageDesc() {
        students.sort(Comparator.comparingDouble(Student::getPercentage).reversed());
    }

    public void sortByRollNo() {
        students.sort(Comparator.comparingInt(Student::getRollNo));
    }

    // ── Accessors ──────────────────────────────────────

    public ArrayList<Student> getStudents() { return students; }

    private void saveStudents() { FileHandler.saveStudents(students); }

    // ═══════════════════════════════════════════════════
    //  PROFESSOR CRUD
    // ═══════════════════════════════════════════════════

    public boolean addProfessor(Professor professor) {
        for (Professor p : professors) {
            if (p.getUsername().equals(professor.getUsername())) {
                System.out.println("❌ Professor username '" + professor.getUsername() + "' already exists.");
                return false;
            }
        }
        professors.add(professor);
        saveProfessors();
        System.out.println("✅ Professor '" + professor.getUsername() + "' added.");
        return true;
    }

    public boolean deleteProfessor(String username) {
        Professor target = null;
        for (Professor p : professors) {
            if (p.getUsername().equals(username)) { target = p; break; }
        }
        if (target == null) return false;
        professors.remove(target);
        saveProfessors();
        return true;
    }

    public Professor findProfessor(String username, String password) {
        for (Professor p : professors) {
            if (p.login(username, password)) return p;
        }
        return null;
    }

    public ArrayList<Professor> getProfessors() { return professors; }

    private void saveProfessors() { FileHandler.saveProfessors(professors); }
}
