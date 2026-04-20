import controller.ResultManager;
import model.BacklogStudent;
import model.Professor;
import model.Student;

import java.util.Scanner;

/**
 * Entry point for the Student Result Management System.
 * Provides a console-based menu for Professor and Student roles.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ResultManager resultManager = new ResultManager();

    // Default professor credentials
    private static final Professor professor = new Professor("admin", "admin123");

    // ─── Main ──────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║     STUDENT RESULT MANAGEMENT SYSTEM                ║");
        System.out.println("║     Built with Java · OOP · File Handling           ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println("\n┌──────────────────────────┐");
            System.out.println("│      SELECT ROLE         │");
            System.out.println("├──────────────────────────┤");
            System.out.println("│  1. Professor Login      │");
            System.out.println("│  2. Student Login        │");
            System.out.println("│  3. Exit                 │");
            System.out.println("└──────────────────────────┘");
            System.out.print("Enter choice: ");

            int choice = readInt();
            switch (choice) {
                case 1:
                    professorLogin();
                    break;
                case 2:
                    studentLogin();
                    break;
                case 3:
                    running = false;
                    System.out.println("\nGoodbye! 👋");
                    break;
                default:
                    System.out.println("❌ Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    // ─── Professor Flow ────────────────────────────────

    private static void professorLogin() {
        System.out.println("\n── Professor Login ──");
        System.out.print("Username: ");
        String user = scanner.nextLine().trim();
        System.out.print("Password: ");
        String pass = scanner.nextLine().trim();

        if (!professor.login(user, pass)) {
            System.out.println("❌ Invalid credentials.");
            return;
        }

        System.out.println("✅ Welcome, Professor " + professor.getUsername() + "!");
        professorMenu();
        professor.logout();
    }

    private static void professorMenu() {
        boolean active = true;
        while (active) {
            System.out.println("\n┌──────────────────────────────┐");
            System.out.println("│      PROFESSOR MENU          │");
            System.out.println("├──────────────────────────────┤");
            System.out.println("│  1. Add Student              │");
            System.out.println("│  2. Add Backlog Student      │");
            System.out.println("│  3. Update Student Marks     │");
            System.out.println("│  4. Delete Student           │");
            System.out.println("│  5. View All Students        │");
            System.out.println("│  6. Sort by Percentage       │");
            System.out.println("│  7. Sort by Roll Number      │");
            System.out.println("│  8. Search Student           │");
            System.out.println("│  9. Logout                   │");
            System.out.println("└──────────────────────────────┘");
            System.out.print("Enter choice: ");

            int choice = readInt();
            switch (choice) {
                case 1: addStudent(false); break;
                case 2: addStudent(true); break;
                case 3: updateMarks(); break;
                case 4: deleteStudent(); break;
                case 5: resultManager.viewAllStudents(); break;
                case 6: resultManager.sortByPercentageDesc(); resultManager.viewAllStudents(); break;
                case 7: resultManager.sortByRollNo(); resultManager.viewAllStudents(); break;
                case 8: searchStudent(); break;
                case 9: active = false; break;
                default: System.out.println("❌ Invalid choice.");
            }
        }
    }

    private static void addStudent(boolean isBacklog) {
        System.out.println("\n── Add " + (isBacklog ? "Backlog " : "") + "Student ──");

        System.out.print("Roll Number : ");
        int rollNo = readInt();
        System.out.print("Name        : ");
        String name = scanner.nextLine().trim();
        System.out.print("Subject 1   : ");
        int m1 = readInt();
        System.out.print("Subject 2   : ");
        int m2 = readInt();
        System.out.print("Subject 3   : ");
        int m3 = readInt();

        // Generate default student credentials: username = "student<rollNo>", password = "pass<rollNo>"
        String username = "student" + rollNo;
        String password = "pass" + rollNo;

        int[] marks = {m1, m2, m3};
        Student student;
        if (isBacklog) {
            student = new BacklogStudent(username, password, rollNo, name, marks);
        } else {
            student = new Student(username, password, rollNo, name, marks);
        }

        resultManager.addStudent(student);
        System.out.println("ℹ  Student login credentials → Username: " + username + " | Password: " + password);
    }

    private static void updateMarks() {
        System.out.println("\n── Update Student Marks ──");
        System.out.print("Enter Roll Number: ");
        int rollNo = readInt();
        System.out.print("New Subject 1 marks: ");
        int m1 = readInt();
        System.out.print("New Subject 2 marks: ");
        int m2 = readInt();
        System.out.print("New Subject 3 marks: ");
        int m3 = readInt();

        resultManager.updateMarks(rollNo, new int[]{m1, m2, m3});
    }

    private static void deleteStudent() {
        System.out.println("\n── Delete Student ──");
        System.out.print("Enter Roll Number to delete: ");
        int rollNo = readInt();
        resultManager.deleteStudent(rollNo);
    }

    private static void searchStudent() {
        System.out.println("\n── Search Student ──");
        System.out.print("Enter Roll Number: ");
        int rollNo = readInt();
        Student student = resultManager.findByRollNo(rollNo);
        if (student == null) {
            System.out.println("❌ Student not found.");
        } else {
            student.viewResult();
        }
    }

    // ─── Student Flow ──────────────────────────────────

    private static void studentLogin() {
        System.out.println("\n── Student Login ──");
        System.out.print("Username: ");
        String user = scanner.nextLine().trim();
        System.out.print("Password: ");
        String pass = scanner.nextLine().trim();

        Student student = resultManager.findByCredentials(user, pass);
        if (student == null) {
            System.out.println("❌ Invalid credentials or student not found.");
            return;
        }

        System.out.println("✅ Welcome, " + student.getName() + "!");
        student.viewResult();
        System.out.println("\nPress Enter to logout...");
        scanner.nextLine();
        student.logout();
    }

    // ─── Utility ───────────────────────────────────────

    /**
     * Reads an integer from stdin, consuming the trailing newline.
     */
    private static int readInt() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("❌ Please enter a valid number: ");
            }
        }
    }
}
