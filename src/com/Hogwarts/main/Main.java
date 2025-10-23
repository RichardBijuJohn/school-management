package com.Hogwarts.main;

import com.Hogwarts.DAO.*;
import com.Hogwarts.model.*;
import com.Hogwarts.ui.ClassUI;
import com.Hogwarts.ui.StudentUI;
import com.Hogwarts.ui.TeacherUI;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;



public class Main {
    private JFrame frame;
    private final UserDAO userDAO = new UserDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().showLogin());
    }


    private void showLogin(){
        frame = new JFrame("School Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 520);
        frame.setLayout(new BorderLayout());

        // Top panel for logo and title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 220)); // beige
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));


        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(UIManager.getIcon("OptionPane.informationIcon"));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);


        JLabel title = new JLabel("Hogwarts School Management", JLabel.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 24));
        title.setForeground(new Color(44, 62, 80));


        JLabel subtitle = new JLabel("Login to your account", JLabel.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 149, 237));
        subtitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));


        topPanel.add(iconLabel, BorderLayout.NORTH);
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(subtitle, BorderLayout.SOUTH);


        frame.add(topPanel, BorderLayout.NORTH);

        // Main login panel
        JPanel p = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(245, 245, 220)); // beige
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        


        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Username:"), gbc);
        JTextField tfUser = new JTextField(20);
        tfUser.setFont(new Font("Open Sans", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0;
        p.add(tfUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        p.add(new JLabel("Password:"), gbc);
        JPasswordField tfPass = new JPasswordField();
        tfPass.setFont(new Font("Open Sans", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 1;
        p.add(tfPass, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 20;
        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(100, 149, 237));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        p.add(btnLogin, gbc);


        frame.add(p, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> {
            String u = tfUser.getText().trim(); String pw = new String(tfPass.getPassword()).trim();
            try {
                User user = userDAO.findByUsernameAndPassword(u, pw);
                if (user == null) {
                    showStyledError(frame, 
                        "The username or password you entered is incorrect.\nPlease try again.", 
                        "Login Failed");
                    tfPass.setText("");
                    tfUser.requestFocus();
                    return;
                }
                frame.dispose();
                switch(user.getRole()){
                    case "ADMIN": showAdminPanel(); break;
                    case "TEACHER": showTeacherPanel(user); break;
                    case "STUDENT": showStudentPanel(user); break;
                    default: JOptionPane.showMessageDialog(null,"Unknown role");
                }
            } catch (SQLException ex){ showErr(ex); }
        });


        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    // Styled error dialog
    private void showStyledError(JFrame parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    private void showAdminPanel(){
        JFrame f = new JFrame("Admin Panel");
        f.setSize(1000, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        // --- Home Tab ---
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(new Color(245, 245, 220)); // beige
        homePanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel welcomeLabel = new JLabel("Hogwarts School Management System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 30));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("<html><div style='text-align:center;'>"
            + "This school is not a normal school we give admission to the kids having different magical skills<br>"
            + "Harry Potter , Ron Weasley and Hermione are a proud alumini.<br>"
                +"Albus dumbledore the headmaster is a very encouraging person.<br> He helps students and supports them always.<br>"
            +"Voldemort is our principal  '_'<br>"
            + "<b>Contact:</b> hogwarts@school.edu<br>"
            + "<b>Phone:</b>1234567890<br>"
            + "</div></html>", JLabel.CENTER);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        homePanel.add(welcomeLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        homePanel.add(infoLabel);


        // --- Students Tab ---
        StudentUI studentUI = new StudentUI(studentDAO, userDAO);
        JPanel studentPanel = studentUI.getPanel();
        studentPanel.setBackground(new Color(245, 245, 220)); // beige

        // --- Teachers Tab ---
        TeacherUI teacherUI = new TeacherUI(teacherDAO, userDAO);
        JPanel teacherPanel = teacherUI.getPanel();
        teacherPanel.setBackground(new Color(245, 245, 220)); // beige

        // --- Classes Tab ---
        ClassUI classUI = new ClassUI(studentDAO, teacherDAO);
        JPanel classPanel = classUI.getPanel();
        classPanel.setBackground(new Color(245, 245, 220)); // beige

        tabs.addTab("Home", homePanel);
        tabs.addTab("Students", studentPanel);
        tabs.addTab("Teachers", teacherPanel);
        tabs.addTab("Classes", classPanel);

        // Add logout button to the top right
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 220));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(245, 245, 220));
        logoutPanel.add(logoutBtn);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        // Add the tabbed pane below the logout button
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 220));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);

        f.setContentPane(mainPanel);

        logoutBtn.addActionListener(e -> {
            f.dispose(); // Properly dispose the frame
            // Always create a new Main instance to show login
            new Main().showLogin();
        });

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // ----------------- Teacher Panel -----------------
    private void showTeacherPanel(User user){
        JFrame f = new JFrame("Teacher Panel");
        f.setSize(800, 550);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        try {
            Teacher t = teacherDAO.getById(user.getRefId());
            tabs.addTab("My Class students", teacherStudentsPanel(t, f));
        } catch(SQLException ex){ showErr(ex); }

        // Add logout button to the top right
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 220));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(new Color(245, 245, 220));
        logoutPanel.add(logoutBtn);
        topPanel.add(logoutPanel, BorderLayout.EAST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 220));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);

        f.setContentPane(mainPanel);

        logoutBtn.addActionListener(e -> {
            f.dispose(); // Properly dispose the frame
            // Always create a new Main instance to show login
            new Main().showLogin();
        });

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


    // Enhanced teacherStudentsPanel with search and back button
    private JPanel teacherStudentsPanel(Teacher teacher, JFrame parentFrame){
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 245, 220)); // beige

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(new Color(245, 245, 220)); // beige
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        
        searchPanel.add(new JLabel("Search Student by ID/Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        p.add(searchPanel, BorderLayout.NORTH);

        // Remove "Marks" column
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Age","Class"}, 0
        ) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel ctrl = new JPanel();
        ctrl.setBackground(new Color(245, 245, 220)); // beige
        JButton enterMarks = new JButton("Enter/Update Marks");
        ctrl.add(enterMarks);
        p.add(ctrl, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            try {
                model.setRowCount(0);
                if (teacher.getClassAssigned()==null){
                    JOptionPane.showMessageDialog(null,"No class assigned.");
                    return;
                }
                java.util.List<Student> list = studentDAO.getByClass(teacher.getClassAssigned());
                for (Student s: list)
                    model.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getClassNo()});
            } catch(SQLException ex){ showErr(ex); }
        };
        refresh.run();

        // Search logic
        searchBtn.addActionListener(e -> {
            String query = searchField.getText().trim().toLowerCase();
            try {
                model.setRowCount(0);
                java.util.List<Student> list = studentDAO.getByClass(teacher.getClassAssigned());
                for (Student s : list) {
                    if (query.isEmpty() ||
                        String.valueOf(s.getId()).equalsIgnoreCase(query) ||
                        s.getName().toLowerCase().contains(query)) {
                        model.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getClassNo()});
                    }
                }
            } catch(SQLException ex){ showErr(ex); }
        });


        enterMarks.addActionListener(e -> {
    int r = table.getSelectedRow();
    if (r == -1) {
        JOptionPane.showMessageDialog(null, "Select a student");
        return;
    }
    int id = Integer.parseInt(model.getValueAt(r, 0).toString());
    Student s = null;
    try {
        for (Student st : studentDAO.getByClass(teacher.getClassAssigned()))
            if (st.getId() == id) s = st;
    } catch (SQLException ex) { showErr(ex); return; }

    if (s == null) {
        JOptionPane.showMessageDialog(null, "You can only edit students of your class.");
        return;
    }

    // Dropdown for exam selection
    String[] exams = {"First Internal", "Second Internal", "Term Exam"};
    JComboBox<String> examBox = new JComboBox<>(exams);

    // Subjects list
    String[] subjects = {
        "English", "Maths", "Science", "Social", "Hindi",
        "Computer", "Physics", "Chemistry", "Biology", "History"
    };

    // Create a map of subject -> JTextField
    java.util.Map<String, JTextField> subjectFields = new java.util.HashMap<>();

    JPanel marksPanel = new JPanel(new GridLayout(subjects.length + 1, 2, 8, 8));
    marksPanel.add(new JLabel("Examination:"));
    marksPanel.add(examBox);

    // Add text fields for each subject
    for (String subject : subjects) {
        marksPanel.add(new JLabel(subject + ":"));
        JTextField field = new JTextField(5);
        subjectFields.put(subject, field);
        marksPanel.add(field);
    }

    // Helper method to populate marks from string
    final Student selectedStudent = s;
    Runnable populateMarks = () -> {
        String examSel = (String) examBox.getSelectedItem();
        String existingMarks = "";
        
        if (examSel.equals("First Internal")) {
            existingMarks = selectedStudent.getFirstInternal();
        } else if (examSel.equals("Second Internal")) {
            existingMarks = selectedStudent.getSecondInternal();
        } else if (examSel.equals("Term Exam")) {
            existingMarks = selectedStudent.getTermExam();
        }
        
        // Clear all fields first
        for (JTextField field : subjectFields.values()) {
            field.setText("");
        }
        
        // Parse existing marks and populate fields
        if (existingMarks != null && !existingMarks.trim().isEmpty()) {
            String[] markPairs = existingMarks.split(",");
            for (String pair : markPairs) {
                pair = pair.trim();
                if (pair.contains(":")) {
                    String[] parts = pair.split(":");
                    if (parts.length == 2) {
                        String subject = parts[0].trim();
                        String mark = parts[1].trim();
                        if (subjectFields.containsKey(subject)) {
                            subjectFields.get(subject).setText(mark);
                        }
                    }
                }
            }
        }
    };

    // Populate marks when exam is changed
    examBox.addActionListener(evt -> populateMarks.run());
    
    // Initial population for first exam
    populateMarks.run();

    // Scrollable dialog if needed
    JScrollPane scroll = new JScrollPane(marksPanel);
    scroll.setPreferredSize(new Dimension(400, 350));

    int res = JOptionPane.showConfirmDialog(
        null, scroll, "Enter/Update Marks",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
    );

    if (res == JOptionPane.OK_OPTION) {
        String examSel = (String) examBox.getSelectedItem();

        try {
            // Collect marks entered for each subject
            StringBuilder marksSummary = new StringBuilder();
            for (String subject : subjects) {
                String mark = subjectFields.get(subject).getText().trim();
                if (mark.isEmpty()) mark = "0";
                marksSummary.append(subject).append(":").append(mark).append(", ");
            }

            // Save marks summary to DB
            // You can store this as a single string in one column per exam
            if (examSel.equals("First Internal")) s.setFirstInternal(marksSummary.toString());
            else if (examSel.equals("Second Internal")) s.setSecondInternal(marksSummary.toString());
            else if (examSel.equals("Term Exam")) s.setTermExam(marksSummary.toString());

            studentDAO.update(s);
            refresh.run();

        } catch (SQLException ex) {
            showErr(ex);
        }
    }
});


        return p;
    }
    // ----------------- Student Panel -----------------
    private void showStudentPanel(User user){
        JFrame f = new JFrame("Student Panel");
        f.setSize(600,400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 245, 220)); // beige

        JButton backBtn = new JButton("Logout");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(new Color(245, 245, 220)); // beige
        topPanel.add(backBtn);
        p.add(topPanel, BorderLayout.NORTH);

        try {
            Student s = null;
            for (Student st: studentDAO.getAll()) if (st.getId()==user.getRefId()) s=st;
            if (s==null){ JOptionPane.showMessageDialog(null,"Student profile not found"); return; }
            JTextArea ta = new JTextArea();
            ta.setEditable(false);
            ta.setText("Name: "+s.getName()+"\nAge: "+s.getAge()+"\nClass: "+s.getClassNo()+"\nAddress: "+s.getAddress()+"\nMarks: "+s.getMarks());
            p.add(new JScrollPane(ta), BorderLayout.CENTER);
        } catch(SQLException ex){ showErr(ex); }

        backBtn.addActionListener(e -> {
            f.setVisible(false);
            showLogin();
        });

        f.add(p);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


    // ----------------- Small helper forms -----------------
// TeacherForm used in admin to add/edit teacher + create login credentials
class TeacherForm {
    private JPanel panel;
    private JTextField nameField = new JTextField(20);
    private JTextField phoneField = new JTextField(15);
    private JTextField subjectField = new JTextField(20);
    private JTextField qualField = new JTextField(20);
    private JComboBox<Integer> classBox = new JComboBox<>();
    private JTextField usernameField = new JTextField(12);
    private JTextField passwordField = new JTextField(12);
    private JRadioButton maleBtn = new JRadioButton("Male");
    private JRadioButton femaleBtn = new JRadioButton("Female");
    private ButtonGroup genderGroup = new ButtonGroup();

    public TeacherForm(Teacher t){
        panel = new JPanel(new GridLayout(11,2,5,5));
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Subject:")); panel.add(subjectField);
        panel.add(new JLabel("Phone Number:")); panel.add(phoneField);
        panel.add(new JLabel("Qualification:")); panel.add(qualField);
        panel.add(new JLabel("Class Assigned (optional):"));
        classBox.addItem(null); for(int i=1;i<=10;i++) classBox.addItem(i); panel.add(classBox);
        panel.add(new JLabel("Gender:"));
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderGroup.add(maleBtn); genderGroup.add(femaleBtn);
        genderPanel.add(maleBtn); genderPanel.add(femaleBtn);
        panel.add(genderPanel);
        panel.add(new JLabel("Login username:")); panel.add(usernameField);
        panel.add(new JLabel("Login password:")); panel.add(passwordField);
        if (t!=null){
            nameField.setText(t.getName());
            subjectField.setText(t.getSubject());
            qualField.setText(t.getQualification());
            // phoneField.setText(t.getPhone()); // Uncomment if Teacher model has phone
            if (t.getClassAssigned()!=null) classBox.setSelectedItem(t.getClassAssigned());
            else classBox.setSelectedItem(null);
            if ("Male".equalsIgnoreCase(t.getGender())) maleBtn.setSelected(true);
            else if ("Female".equalsIgnoreCase(t.getGender())) femaleBtn.setSelected(true);
            // Optionally set usernameField and passwordField if you store them in Teacher
        } else {
            maleBtn.setSelected(true);
        }
    }
    public JPanel getPanel(){ return panel; }
    public Teacher toTeacher(){
        Integer classAssigned = (Integer)classBox.getSelectedItem();
        String gender = maleBtn.isSelected() ? "Male" : "Female";
        // Fix: Match Teacher constructor signature (remove phoneField if not in model)
        return new Teacher(
            nameField.getText().trim(),
            gender,
            phoneField.getText().trim(),
            subjectField.getText().trim(),
            qualField.getText().trim(),
            classAssigned
        );
    }
    public String getUsername() { return usernameField.getText().trim(); }
    public String getPassword() { return passwordField.getText().trim(); }
}


// StudentForm used in admin to add/edit student + create login credentials
class StudentForm {
    private JPanel panel;
    private JTextField admissionNumberField = new JTextField();
    private JTextField nameField = new JTextField(20);
    private JTextField ageField = new JTextField(4);
    private JComboBox<Integer> classBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10});
    private JTextField addressField = new JTextField(30);
    private JTextField firstInternalField = new JTextField(30);
    private JTextField secondInternalField = new JTextField(30);
    private JTextField termExamField = new JTextField(30);
    private JTextField fatherNameField = new JTextField(20);
    private JTextField fatherNumberField = new JTextField(20);
    private JTextField dobField = new JTextField(12);
    private JTextField usernameField = new JTextField(12);
    private JTextField passwordField = new JTextField(12);
    private JRadioButton maleBtn = new JRadioButton("Male");
    private JRadioButton femaleBtn = new JRadioButton("Female");
    private ButtonGroup genderGroup = new ButtonGroup();

    public StudentForm(Student s){
        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Admission Number:"), gbc);
        gbc.gridx = 1;
        panel.add(admissionNumberField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1;
        panel.add(ageField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Class:"), gbc);
        gbc.gridx = 1;
        panel.add(classBox, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderGroup.add(maleBtn); genderGroup.add(femaleBtn);
        genderPanel.add(maleBtn); genderPanel.add(femaleBtn);
        panel.add(genderPanel, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("First Internal:"), gbc);
        gbc.gridx = 1;
        panel.add(firstInternalField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Second Internal:"), gbc);
        gbc.gridx = 1;
        panel.add(secondInternalField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Term Exam:"), gbc);
        gbc.gridx = 1;
        panel.add(termExamField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Father's Name:"), gbc);
        gbc.gridx = 1;
        panel.add(fatherNameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Father's Number:"), gbc);
        gbc.gridx = 1;
        panel.add(fatherNumberField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("DOB:"), gbc);
        gbc.gridx = 1;
        panel.add(dobField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Login username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        panel.add(new JLabel("Login password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        if (s!=null){
            admissionNumberField.setText(s.getAdmissionNumber());
            nameField.setText(s.getName());
            ageField.setText(String.valueOf(s.getAge()));
            classBox.setSelectedItem(s.getClassNo());
            addressField.setText(s.getAddress());
            firstInternalField.setText(s.getFirstInternal());
            secondInternalField.setText(s.getSecondInternal());
            termExamField.setText(s.getTermExam());
            fatherNameField.setText(s.getFatherName());
            fatherNumberField.setText(s.getFatherNumber());
            dobField.setText(s.getDob());
            if ("Male".equalsIgnoreCase(s.getGender())) maleBtn.setSelected(true);
            else if ("Female".equalsIgnoreCase(s.getGender())) femaleBtn.setSelected(true);
        } else {
            maleBtn.setSelected(true);
        }
    }
    public JPanel getPanel(){ return panel; }
    public Student toStudent(){
        int age=0; try{ age=Integer.parseInt(ageField.getText().trim()); } catch(Exception e){}
        String gender = maleBtn.isSelected() ? "Male" : "Female";
        return new Student(
            admissionNumberField.getText().trim(),
            nameField.getText().trim(),
            age,
            (Integer) classBox.getSelectedItem(),
            addressField.getText().trim(),
            firstInternalField.getText().trim(),
            secondInternalField.getText().trim(),
            termExamField.getText().trim(),
            fatherNameField.getText().trim(),
            fatherNumberField.getText().trim(),
            dobField.getText().trim(),
            gender
        );
    }
    public String getUsername() { return usernameField.getText().trim(); }
    public String getPassword() { return passwordField.getText().trim(); }
    }

    // - Utility
    private void showErr(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); }

    // Student profile dialog for admin
    private void showStudentProfileDialog(Student s, Runnable refreshStudents) {
        JDialog dialog = new JDialog((Frame) null, "Student Profile", true);
        JPanel panel = new JPanel(new GridLayout(0,2,8,8));
        panel.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
        panel.add(new JLabel("ID:")); panel.add(new JLabel(String.valueOf(s.getId())));
        panel.add(new JLabel("Admission Number:")); panel.add(new JLabel(s.getAdmissionNumber()));
        panel.add(new JLabel("Name:")); panel.add(new JLabel(s.getName()));
        panel.add(new JLabel("Class:")); panel.add(new JLabel(String.valueOf(s.getClassNo())));
        panel.add(new JLabel("Age:")); panel.add(new JLabel(String.valueOf(s.getAge())));
        panel.add(new JLabel("Gender:")); panel.add(new JLabel(s.getGender()));
        panel.add(new JLabel("Address:")); panel.add(new JLabel(s.getAddress()));
        panel.add(new JLabel("First Internal:")); panel.add(new JLabel(s.getFirstInternal()));
        panel.add(new JLabel("Second Internal:")); panel.add(new JLabel(s.getSecondInternal()));
        panel.add(new JLabel("Term Exam:")); panel.add(new JLabel(s.getTermExam()));
        panel.add(new JLabel("Father's Name:")); panel.add(new JLabel(s.getFatherName()));
        panel.add(new JLabel("Father's Number:")); panel.add(new JLabel(s.getFatherNumber()));
        panel.add(new JLabel("DOB:")); panel.add(new JLabel(s.getDob()));

        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JPanel btnPanel = new JPanel();
        btnPanel.add(updateBtn); btnPanel.add(deleteBtn);

        JPanel content = new JPanel(new BorderLayout());
        content.add(panel, BorderLayout.CENTER);
        content.add(btnPanel, BorderLayout.SOUTH);

        dialog.getContentPane().add(content);
        dialog.pack();
        dialog.setLocationRelativeTo(null);

        updateBtn.addActionListener(e -> {
            StudentForm form = new StudentForm(s);
            int r = JOptionPane.showConfirmDialog(dialog, form.getPanel(), "Update Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r == JOptionPane.OK_OPTION) {
                try {
                    Student ns = form.toStudent();
                    ns.setId(s.getId());
                    studentDAO.update(ns);
                    refreshStudents.run();
                    JOptionPane.showMessageDialog(dialog, "Updated successfully.");
                    dialog.dispose();
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                dialog,
                "Are you sure you want to delete this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    studentDAO.delete(s.getId());
                    refreshStudents.run();
                    JOptionPane.showMessageDialog(dialog, "Record deleted.");
                    dialog.dispose();
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        dialog.setVisible(true);
    }
}


