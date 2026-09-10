import java.io.*;
import java.util.*;

/**
 * Student Course Portal CLI Application */
public class Main {
    private static final String DATA_FILE = "portal_data.txt";
    private static final Scanner scanner = new Scanner(System.in);
    
    private static final Map<String, Student> students = new HashMap<>();
    private static final Map<String, Course> courses = new HashMap<>();

    public static void main(String[] args) {
        loadData();
        seedInitialCoursesIfEmpty();

        System.out.println("=========================================");
        System.out.println("   WELCOME TO STUDENT COURSE PORTAL      ");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> registerStudent();
                case "2" -> listCourses();
                case "3" -> enrollInCourse();
                case "4" -> dropCourse();
                case "5" -> viewStudentProfile();
                case "6" -> {
                    saveData();
                    System.out.println("\nData saved successfully. Exiting portal. Goodbye!");
                    running = false;
                }
                default -> System.out.println("\nInvalid option. Please choose between 1 and 7.");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("\n-------------------------------------------");
        System.out.println("Main Menu:");
        System.out.println("1. Register New Student");
        System.out.println("2. View Available Courses");
        System.out.println("3. Enroll Student in Course");
        System.out.println("4. Drop Course for Student");
        System.out.println("5. View Student Profile & Enrolled Courses");
        System.out.println("6. Save & Exit");
        System.out.print("Enter choice (1-6): ");
    }

    private static void registerStudent() {
        System.out.print("\nEnter Student ID: ");
        String id = scanner.nextLine().trim();
        
        if (id.isEmpty()) {
            System.out.println("Student ID cannot be empty.");
            return;
        }

        boolean exists = students.keySet().stream().anyMatch(existingId -> existingId.equalsIgnoreCase(id));

        if (exists) {
            System.out.println("A student names Registration number " + id + "already exists.");
            return;
        }

        System.out.print("Enter Student Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Student name cannot be empty.");
            return;
        }

        students.put(id, new Student(id, name));
        System.out.println("Student " + name + " registered successfully");
    }

    private static void listCourses() {
        System.out.println("\n----- Available Courses -----");
        if (courses.isEmpty()) {
            System.out.println("No courses currently available.");
            return;
        }
        for (Course course : courses.values()) {
            System.out.println(course);
        }
    }

    private static void enrollInCourse() {
        System.out.print("\nEnter Student ID: ");
        String studentId = scanner.nextLine().trim();
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("Student ID not found. Please register first.");
            return;
        }

        listCourses();
        System.out.print("Enter Course Code to Enroll: ");
        String courseCode = scanner.nextLine().trim().toUpperCase();
        Course course = courses.get(courseCode);

        if (course == null) {
            System.out.println("Course code does not exist.");
            return;
        }

        if (student.enroll(course)) {
            System.out.println("Enrolled " + student.getName() + " in " + course.getName());
        } else {
            System.out.println("Student is already enrolled in this course or capacity is full.");
        }
    }

    private static void dropCourse() {
        System.out.print("\nEnter Student ID: ");
        String studentId = scanner.nextLine().trim();
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("Student ID not found.");
            return;
        }

        System.out.print("Enter Course Code to Drop: ");
        String courseCode = scanner.nextLine().trim().toUpperCase();

        if (student.drop(courseCode)) {
            System.out.println("Course dropped successfully.");
        } else {
            System.out.println("Student was not enrolled in course " + courseCode);
        }
    }

    private static void viewStudentProfile() {
        System.out.print("\nEnter Student ID: ");
        String studentId = scanner.nextLine().trim();
        Student student = students.get(studentId);
        if (student == null) {
            System.out.println("Student ID not found.");
            return;
        }

        System.out.println("\n=========================================");
        System.out.println("Student Profile");
        System.out.println("ID: " + student.getId());
        System.out.println("Name: " + student.getName());
        System.out.println("Enrolled Courses:");
        if (student.getEnrolledCourses().isEmpty()) {
            System.out.println(" You have not enrolled in a course.");
        } else {
            for (Course c : student.getEnrolledCourses()) {
                System.out.println("  - " + c.getCode() + ": " + c.getName() + " (" + c.getCredits() + " Credits)");
            }
        }
        System.out.println("=========================================");
    }
    
    private static void seedInitialCoursesIfEmpty() {
        if (courses.isEmpty()) {
            courses.put("CSE3003", new Course("CSE3003", "Operating System", 4, 120));
            courses.put("CSA2003", new Course("CSA2003", "Digital Logic and Computer Architecture", 4, 120));
            courses.put("MAT2002", new Course("MAT2002", "Discrete Maths and Graph Theory", 4, 75));
            courses.put("CSE3011", new Course("CSE3011", "Python Programming", 3, 120));
            courses.put("CSE2006", new Course("CSE2006","Programming in Java",3,120));
        }
    }

    private static void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            writer.println("# COURSES");
            for (Course c : courses.values()) {
                String code = (c.getCode() != null && !c.getCode().trim().isEmpty()) ? c.getCode() : "Not Available";
                String name = (c.getName() != null && !c.getName().trim().isEmpty()) ? c.getName() : "Not Available";
                String credits = String.valueOf(c.getCredits());
                String capacity = String.valueOf(c.getCapacity());

                writer.println(code + ";" + name + ";" + credits + ";" + capacity);
            }

            writer.println("# STUDENTS");
            for (Student s : students.values()) {
                String id = (s.getId() != null && !s.getId().trim().isEmpty()) ? s.getId() : "Not Available right now.";
                String name = (s.getName() != null && !s.getName().trim().isEmpty()) ? s.getName()
                        : "Not Available right now.";

                StringBuilder enrolledCodes = new StringBuilder();
                if (s.getEnrolledCourses() != null && !s.getEnrolledCourses().isEmpty()) {
                    for (Course ec : s.getEnrolledCourses()) {
                        if (enrolledCodes.length() > 0)
                            enrolledCodes.append(",");
                        String cCode = (ec.getCode() != null && !ec.getCode().trim().isEmpty()) ? ec.getCode()
                                : "Not Available";
                        enrolledCodes.append(cCode);
                    }
                } else {
                    enrolledCodes.append("Not Available");
                }

                writer.println(id + ";" + name + ";" + enrolledCodes);
            }
        } 
    catch (IOException e) {
        System.out.println("Failed to persist data: " + e.getMessage());
    }
}

    private static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean readingStudents = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.equals("# COURES")) {
                    readingStudents = false;
                    continue;
                } else if (line.equals("# STUDENTS")) {
                    readingStudents = true;
                    continue;
                }

                String[] parts = line.split(";");
                if (!readingStudents) {
                    if (parts.length == 4) {
                        String code = parts[0];
                        String name = parts[1];
                        int credits = Integer.parseInt(parts[2]);
                        int cap = Integer.parseInt(parts[3]);
                        courses.put(code, new Course(code, name, credits, cap));
                    }
                } else {
                    if (parts.length >= 2) {
                        String id = parts[0];
                        String name = parts[1];
                        Student student = new Student(id, name);
                        if (parts.length == 3 && !parts[2].isEmpty()) {
                            String[] courseCodes = parts[2].split(",");
                            for (String code : courseCodes) {
                                Course course = courses.get(code);
                                if (course != null) {
                                    student.enroll(course);
                                }
                            }
                        }
                        students.put(id, student);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading saved data file. Starting with clean state.");
        }
    }
}

class Course {
    private final String code;
    private final String name;
    private final int credits;
    private final int capacity;
    private int enrolledCount;

    public Course(String code, String name, int credits, int capacity) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.capacity = capacity;
        this.enrolledCount = 0;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public int getCapacity() { return capacity; }

    public boolean canEnroll() { return enrolledCount < capacity; }
    public void incrementEnrollment() { enrolledCount++; }
    public void decrementEnrollment() { if (enrolledCount > 0) enrolledCount--; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Credits: %d | Seats: %d/%d", code, name, credits, enrolledCount, capacity);
    }
}

class Student {
    private final String id;
    private final String name;
    private final List<Course> enrolledCourses;

    public Student(String id, String name) {
        this.id = id;
        this.name = name;
        this.enrolledCourses = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Course> getEnrolledCourses() { return enrolledCourses; }

    public boolean enroll(Course course) {
        if (enrolledCourses.contains(course) || !course.canEnroll()) {
            return false;
        }
        enrolledCourses.add(course);
        course.incrementEnrollment();
        return true;
    }

    public boolean drop(String courseCode) {
        for (Course c : enrolledCourses) {
            if (c.getCode().equalsIgnoreCase(courseCode)) {
                enrolledCourses.remove(c);
                c.decrementEnrollment();
                return true;
            }
        }
        return false;
    }
}
