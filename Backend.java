import java.io.*;
import java.util.*;

abstract class User {
    private String username;
    private String password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    String getUsername()               { return username; }
    void   setUsername(String u)       { this.username = u; }
    String getPassword()              { return password; }
    void   setPassword(String p)      { this.password = p; }

    boolean login(String u, String p) { return username.equals(u) && password.equals(p); }
    void    logout()                  { System.out.println(username + " logged out."); }
}

class Student extends User {
    private int       rollNo;
    private String    name;
    private String[]  subjectNames;
    private int[]     marks;
    private double    percentage;
    private String    grade;

    Student(String username, String password, int rollNo, String name,
            String[] subjectNames, int[] marks) {
        super(username, password);
        this.rollNo       = rollNo;
        this.name         = name;
        this.subjectNames = subjectNames;
        this.marks        = marks;
        this.percentage   = calculatePercentage();
        this.grade        = assignGrade();
    }

    int       getRollNo()        { return rollNo; }
    void      setRollNo(int r)   { this.rollNo = r; }
    String    getName()          { return name; }
    void      setName(String n)  { this.name = n; }
    String[]  getSubjectNames()  { return subjectNames; }
    int[]     getMarks()         { return marks; }
    double    getPercentage()    { return percentage; }
    String    getGrade()         { return grade; }

    void setMarks(int[] marks) {
        this.marks      = marks;
        this.percentage = calculatePercentage();
        this.grade      = assignGrade();
    }

    double calculatePercentage() {
        if (marks == null || marks.length == 0) return 0;
        int total = 0;
        for (int m : marks) total += m;
        return Math.round((total * 100.0) / (marks.length * 100));
    }

    String assignGrade() {
        double p = calculatePercentage();
        if (p >= 90) return "A+";
        if (p >= 80) return "A";
        if (p >= 70) return "B";
        if (p >= 60) return "C";
        if (p >= 50) return "D";
        if (p >= 40) return "E";
        return "F";
    }

    String getType() { return "Normal"; }

    String toCSV() {
        StringBuilder ms = new StringBuilder();
        for (int i = 0; i < marks.length; i++) {
            if (i > 0) ms.append(";");
            ms.append(marks[i]);
        }
        return rollNo + "," + name + "," + String.join(";", subjectNames) + ","
                + ms + "," + String.format("%.1f", percentage) + "," + grade + ","
                + getType() + "," + getUsername() + "," + getPassword();
    }
}

class BacklogStudent extends Student {
    static final int PASS_MARK = 30;

    private int          backlogCount;
    private List<String> backlogSubjects;

    BacklogStudent(String username, String password, int rollNo, String name,
                   String[] subjectNames, int[] marks) {
        super(username, password, rollNo, name, subjectNames, marks);
        backlogSubjects = new ArrayList<>();
        detectBacklogs();
    }

    int          getBacklogCount()    { return backlogCount; }
    List<String> getBacklogSubjects() { return backlogSubjects; }

    private void detectBacklogs() {
        backlogSubjects.clear();
        int[] m = getMarks();
        String[] s = getSubjectNames();
        for (int i = 0; i < m.length; i++) {
            if (m[i] < PASS_MARK) backlogSubjects.add(s[i]);
        }
        backlogCount = backlogSubjects.size();
    }

    @Override void setMarks(int[] marks) { super.setMarks(marks); detectBacklogs(); }

    @Override
    String assignGrade() {
        for (int m : getMarks()) { if (m < PASS_MARK) return "Backlog"; }
        return super.assignGrade();
    }

    @Override String getType() { return "Backlog"; }

    static boolean hasBacklogs(int[] marks) {
        for (int m : marks) { if (m < PASS_MARK) return true; }
        return false;
    }
}

class Professor extends User {
    private List<String> subjects;

    Professor(String username, String password, List<String> subjects) {
        super(username, password);
        this.subjects = (subjects != null) ? subjects : new ArrayList<>();
    }

    Professor(String username, String password) { this(username, password, new ArrayList<>()); }

    List<String> getSubjects()              { return subjects; }
    void         setSubjects(List<String> s){ this.subjects = s; }

    String toCSV() { return getUsername() + "," + getPassword() + "," + String.join(";", subjects); }
}

class FileHandler {
    private static final String DIR  = "data";
    private static final String STU  = DIR + "/students.csv";
    private static final String PRO  = DIR + "/professors.csv";

    private static void ensureDir() { new File(DIR).mkdirs(); }

    static void saveStudents(ArrayList<Student> students) {
        ensureDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(STU))) {
            pw.println("rollNo,name,subjects,marks,percentage,grade,type,username,password");
            for (Student s : students) pw.println(s.toCSV());
        } catch (IOException e) { System.out.println("❌ " + e.getMessage()); }
    }

    static ArrayList<Student> loadStudents() {
        ArrayList<Student> list = new ArrayList<>();
        File f = new File(STU);
        if (!f.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length < 9) continue;
                int rollNo      = Integer.parseInt(p[0].trim());
                String name     = p[1].trim();
                String[] subs   = p[2].trim().split(";");
                String[] mStrs  = p[3].trim().split(";");
                String type     = p[6].trim();
                String user     = p[7].trim();
                String pass     = p[8].trim();
                int[] marks = new int[mStrs.length];
                for (int i = 0; i < mStrs.length; i++) marks[i] = Integer.parseInt(mStrs[i].trim());

                list.add(type.equalsIgnoreCase("Backlog")
                        ? new BacklogStudent(user, pass, rollNo, name, subs, marks)
                        : new Student(user, pass, rollNo, name, subs, marks));
            }
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
        return list;
    }

    static void saveProfessors(ArrayList<Professor> profs) {
        ensureDir();
        try (PrintWriter pw = new PrintWriter(new FileWriter(PRO))) {
            pw.println("username,password,subjects");
            for (Professor p : profs) pw.println(p.toCSV());
        } catch (IOException e) { System.out.println("❌ " + e.getMessage()); }
    }

    static ArrayList<Professor> loadProfessors() {
        ArrayList<Professor> list = new ArrayList<>();
        File f = new File(PRO);
        if (!f.exists()) {
            list.add(new Professor("admin", "admin123", Arrays.asList("Maths", "Physics", "Chemistry")));
            saveProfessors(list);
            return list;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",");
                if (p.length < 2) continue;
                List<String> subs = new ArrayList<>();
                if (p.length >= 3 && !p[2].trim().isEmpty())
                    for (String s : p[2].trim().split(";")) subs.add(s.trim());
                list.add(new Professor(p[0].trim(), p[1].trim(), subs));
            }
        } catch (Exception e) { System.out.println("❌ " + e.getMessage()); }
        if (list.isEmpty()) {
            list.add(new Professor("admin", "admin123", Arrays.asList("Maths", "Physics", "Chemistry")));
            saveProfessors(list);
        }
        return list;
    }
}

class ResultManager {
    private ArrayList<Student>   students;
    private ArrayList<Professor> professors;

    ResultManager() {
        students   = FileHandler.loadStudents();
        professors = FileHandler.loadProfessors();
    }

    Student addStudent(int rollNo, String name, String[] subjectNames, int[] marks) {
        if (findByRollNo(rollNo) != null) return null;
        String u = "student" + rollNo, p = "pass" + rollNo;
        Student s = BacklogStudent.hasBacklogs(marks)
                ? new BacklogStudent(u, p, rollNo, name, subjectNames, marks)
                : new Student(u, p, rollNo, name, subjectNames, marks);
        students.add(s);
        FileHandler.saveStudents(students);
        return s;
    }

    boolean updateMarks(int rollNo, int[] newMarks) {
        Student old = findByRollNo(rollNo);
        if (old == null) return false;
        boolean needsBacklog = BacklogStudent.hasBacklogs(newMarks);
        boolean isBacklog    = old instanceof BacklogStudent;
        if (needsBacklog == isBacklog) {
            old.setMarks(newMarks);
        } else {
            students.remove(old);
            Student r = needsBacklog
                    ? new BacklogStudent(old.getUsername(), old.getPassword(), old.getRollNo(), old.getName(), old.getSubjectNames(), newMarks)
                    : new Student(old.getUsername(), old.getPassword(), old.getRollNo(), old.getName(), old.getSubjectNames(), newMarks);
            students.add(r);
        }
        FileHandler.saveStudents(students);
        return true;
    }

    boolean deleteStudent(int rollNo) {
        Student s = findByRollNo(rollNo);
        if (s == null) return false;
        students.remove(s);
        FileHandler.saveStudents(students);
        return true;
    }

    Student findByRollNo(int rollNo) {
        for (Student s : students) if (s.getRollNo() == rollNo) return s;
        return null;
    }

    Student findByCredentials(String u, String p) {
        for (Student s : students) if (s.login(u, p)) return s;
        return null;
    }

    ArrayList<BacklogStudent> getBacklogStudents() {
        ArrayList<BacklogStudent> list = new ArrayList<>();
        for (Student s : students) if (s instanceof BacklogStudent) list.add((BacklogStudent) s);
        return list;
    }

    ArrayList<Student> getStudents() { return students; }

    boolean addProfessor(Professor prof) {
        for (Professor p : professors) if (p.getUsername().equals(prof.getUsername())) return false;
        professors.add(prof);
        FileHandler.saveProfessors(professors);
        return true;
    }

    boolean deleteProfessor(String username) {
        Professor t = null;
        for (Professor p : professors) if (p.getUsername().equals(username)) { t = p; break; }
        if (t == null) return false;
        professors.remove(t);
        FileHandler.saveProfessors(professors);
        return true;
    }

    Professor findProfessor(String u, String p) {
        for (Professor pr : professors) if (pr.login(u, p)) return pr;
        return null;
    }

    ArrayList<Professor> getProfessors() { return professors; }
}
