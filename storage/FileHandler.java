package storage;

import model.BacklogStudent;
import model.Student;

import java.io.*;
import java.util.ArrayList;

/**
 * Handles reading/writing student records to a CSV file.
 * CSV format: rollNo,name,marks1,marks2,marks3,percentage,grade,type,username,password
 */
public class FileHandler {

    private static final String FILE_PATH = "data/students.csv";
    private static final String HEADER = "rollNo,name,marks1,marks2,marks3,percentage,grade,type,username,password";

    /**
     * Saves the list of students to a CSV file.
     */
    public static void saveToFile(ArrayList<Student> students) {
        // Ensure parent directory exists
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            pw.println(HEADER);
            for (Student s : students) {
                pw.println(s.toCSV());
            }
            System.out.println("✅ Data saved successfully to " + FILE_PATH);
        } catch (IOException e) {
            System.out.println("❌ Error saving data: " + e.getMessage());
        }
    }

    /**
     * Loads student records from the CSV file.
     * Uses the 'type' column to decide whether to create a Student or BacklogStudent.
     */
    public static ArrayList<Student> loadFromFile() {
        ArrayList<Student> students = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("ℹ  No existing data file found. Starting fresh.");
            return students;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 10) continue;

                int rollNo = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                int m1 = Integer.parseInt(parts[2].trim());
                int m2 = Integer.parseInt(parts[3].trim());
                int m3 = Integer.parseInt(parts[4].trim());
                // percentage (parts[5]) and grade (parts[6]) are recalculated
                String type = parts[7].trim();
                String username = parts[8].trim();
                String password = parts[9].trim();

                int[] marks = {m1, m2, m3};

                Student student;
                if (type.equalsIgnoreCase("Backlog")) {
                    student = new BacklogStudent(username, password, rollNo, name, marks);
                } else {
                    student = new Student(username, password, rollNo, name, marks);
                }
                students.add(student);
            }
            System.out.println("✅ Loaded " + students.size() + " student record(s) from file.");
        } catch (IOException e) {
            System.out.println("❌ Error loading data: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("❌ Error parsing data: " + e.getMessage());
        }

        return students;
    }
}
