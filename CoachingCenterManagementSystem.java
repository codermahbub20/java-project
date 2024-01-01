import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CoachingCenterManagementSystem {

    // Define a Student class with serializable properties
    public static class Student implements Serializable {
        private int studentId;
        private String studentName;

        public Student(int studentId, String studentName) {
            this.studentId = studentId;
            this.studentName = studentName;
        }

        public int getStudentId() {
            return studentId;
        }

        public String getStudentName() {
            return studentName;
        }
    }

    // Define a Course class with serializable properties
    public static class Course implements Serializable {
        public enum CourseType { MAJOR, NON_MAJOR, OPTIONAL }

        private int courseId;
        private String courseName;
        private CourseType courseType;

        public Course(int courseId, String courseName, CourseType courseType) {
            this.courseId = courseId;
            this.courseName = courseName;
            this.courseType = courseType;
        }

        public int getCourseId() {
            return courseId;
        }

        public String getCourseName() {
            return courseName;
        }

        public CourseType getCourseType() {
            return courseType;
        }
    }

    // Define the main class
    public static class MainClass {
        private static final String STUDENT_FILE = "studentData.ser";
        private static final String COURSE_FILE = "courseData.ser";

        // Save object to file
        public static void saveToFile(Object obj, String fileName) throws IOException {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
                oos.writeObject(obj);
            }
        }

        // Load object from file
        public static Object loadFromFile(String fileName) throws IOException, ClassNotFoundException {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
                return ois.readObject();
            }
        }

        public static void main(String[] args) {
            // Create lists to store student and course data
            List<Student> studentList = new ArrayList<>();
            List<Course> courseList = new ArrayList<>();

            // Load existing data from files if available
            try {
                if (new File(STUDENT_FILE).exists()) {
                    studentList = (List<Student>) loadFromFile(STUDENT_FILE);
                }

                if (new File(COURSE_FILE).exists()) {
                    courseList = (List<Course>) loadFromFile(COURSE_FILE);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

            // Example: Creating a student and a course
            Student student = new Student(1, "John Doe");
            Course course = new Course(101, "Introduction to Java", Course.CourseType.MAJOR);

            studentList.add(student);
            courseList.add(course);

            // Save data to files
            try {
                saveToFile(studentList, STUDENT_FILE);
                saveToFile(courseList, COURSE_FILE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Define the input form class
    public static class InputForm extends JFrame {
        private JTextField studentIdField;
        private JTextField studentNameField;
        private JTextField courseIdField;
        private JTextField courseNameField;
        private JComboBox<Course.CourseType> courseTypeComboBox;

        public InputForm(DataTable dataTable) {  // Pass DataTable instance to InputForm
            setTitle("Coaching Center Management System");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 200);

            setLayout(new GridLayout(3, 2));

            // Create input fields and combo box
            studentIdField = new JTextField();
            studentNameField = new JTextField();
            courseIdField = new JTextField();
            courseNameField = new JTextField();
            courseTypeComboBox = new JComboBox<>(Course.CourseType.values());

            // Create save button with action listener
            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Retrieve data from input fields, create objects, and save to file
                    int studentId = Integer.parseInt(studentIdField.getText());
                    String studentName = studentNameField.getText();
                    int courseId = Integer.parseInt(courseIdField.getText());
                    String courseName = courseNameField.getText();
                    Course.CourseType courseType = (Course.CourseType) courseTypeComboBox.getSelectedItem();

                    Student student = new Student(studentId, studentName);
                    Course course = new Course(courseId, courseName, courseType);

                    // Add student and course to lists and save to files
                    List<Student> studentList = new ArrayList<>();
                    List<Course> courseList = new ArrayList<>();

                    try {
                        if (new File(MainClass.STUDENT_FILE).exists()) {
                            studentList = (List<Student>) MainClass.loadFromFile(MainClass.STUDENT_FILE);
                        }

                        if (new File(MainClass.COURSE_FILE).exists()) {
                            courseList = (List<Course>) MainClass.loadFromFile(MainClass.COURSE_FILE);
                        }
                    } catch (IOException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    studentList.add(student);
                    courseList.add(course);

                    try {
                        MainClass.saveToFile(studentList, MainClass.STUDENT_FILE);
                        MainClass.saveToFile(courseList, MainClass.COURSE_FILE);

                        // Notify DataTable to refresh data after saving
                        dataTable.refreshData();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, "Data saved successfully!");
                }
            });

            // Add components to the form
            add(new JLabel("Student ID:"));
            add(studentIdField);
            add(new JLabel("Student Name:"));
            add(studentNameField);
            add(new JLabel("Course ID:"));
            add(courseIdField);
            add(new JLabel("Course Name:"));
            add(courseNameField);
            add(new JLabel("Course Type:"));
            add(courseTypeComboBox);
            add(saveButton);

            setVisible(true);
        }
    }

    // Define the data table class
    public static class DataTable extends JFrame {
        private JTable studentTable;
        private JTable courseTable;
        private DefaultTableModel studentTableModel;
        private DefaultTableModel courseTableModel;

        public DataTable() {
            setTitle("Coaching Center Data Table");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 300);

            // Create JTable models
            studentTableModel = new DefaultTableModel();
            studentTableModel.addColumn("Student ID");
            studentTableModel.addColumn("Student Name");

            courseTableModel = new DefaultTableModel();
            courseTableModel.addColumn("Course ID");
            courseTableModel.addColumn("Course Name");
            courseTableModel.addColumn("Course Type");

            // Create JTables with the data models
            studentTable = new JTable(studentTableModel);
            courseTable = new JTable(courseTableModel);

            // Add JTables to the layout
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Student Data", new JScrollPane(studentTable));
            tabbedPane.addTab("Course Data", new JScrollPane(courseTable));

            // Add refresh button
            JButton refreshButton = new JButton("Refresh Data");
            refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    refreshData();
                }
            });

            // Add components to the frame
            add(tabbedPane, BorderLayout.CENTER);
            add(refreshButton, BorderLayout.SOUTH);

            // Load initial data
            refreshData();

            setVisible(true);
        }

        public void refreshData() {
            // Clear existing data
            studentTableModel.setRowCount(0);
            courseTableModel.setRowCount(0);

            // Load data from files into data models
            List<Student> studentList = new ArrayList<>();
            List<Course> courseList = new ArrayList<>();

            try {
                if (new File(MainClass.STUDENT_FILE).exists()) {
                    studentList = (List<Student>) MainClass.loadFromFile(MainClass.STUDENT_FILE);
                }

                if (new File(MainClass.COURSE_FILE).exists()) {
                    courseList = (List<Course>) MainClass.loadFromFile(MainClass.COURSE_FILE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }

            // Populate JTable models with new data
            for (Student student : studentList) {
                studentTableModel.addRow(new Object[]{student.getStudentId(), student.getStudentName()});
            }

            for (Course course : courseList) {
                courseTableModel.addRow(new Object[]{course.getCourseId(), course.getCourseName(), course.getCourseType()});
            }
        }
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create instances of DataTable first
                DataTable dataTable = new DataTable();
                // Pass the DataTable instance to InputForm
                new InputForm(dataTable);
            }
        });
    }
}
