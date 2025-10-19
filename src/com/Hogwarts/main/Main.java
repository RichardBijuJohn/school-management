package com.Hogwarts.main;

import com.Hogwarts.DAO.*;
import com.Hogwarts.model.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
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
        topPanel.setBackground(new Color(230, 240, 255));
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
                g2.setColor(new Color(240, 248, 255));
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
        homePanel.setBackground(new Color(240, 248, 255));
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
        JPanel studentPanel = new JPanel(new BorderLayout());

        // Use BoxLayout for vertical stacking
        JPanel studentTopPanel = new JPanel();
        studentTopPanel.setLayout(new BoxLayout(studentTopPanel, BoxLayout.Y_AXIS));
        studentTopPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel studentSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField studentSearchField = new JTextField(20);
        JButton studentSearchBtn = new JButton("Search");
        JButton studentBackBtn = new JButton("Logout");
        studentSearchPanel.add(new JLabel("Search Student by ID/Name:"));
        studentSearchPanel.add(studentSearchField);
        studentSearchPanel.add(studentSearchBtn);
        studentSearchPanel.add(studentBackBtn);

        studentTopPanel.add(studentSearchPanel);

        JPanel studentCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addStudent = new JButton("Add Student");
        studentCtrl.add(addStudent);
        studentTopPanel.add(studentCtrl);

        studentPanel.add(studentTopPanel, BorderLayout.NORTH);

        // Update table model
        DefaultTableModel studentModel = new DefaultTableModel(
            new String[]{"ID","Admission_Number", "Name", "Class", "Age", "Gender"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable studentTable = new JTable(studentModel);
        studentTable.setRowHeight(28);
        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane studentScroll = new JScrollPane(studentTable);
        studentPanel.add(studentScroll, BorderLayout.CENTER);

        Runnable refreshStudents = () -> {
            try {
                studentModel.setRowCount(0);
                for (Student s : studentDAO.getAll())
                    studentModel.addRow(new Object[]{
                        s.getId(),
                        s.getAdmissionNumber(),
                        s.getName(),
                        s.getClassNo(),
                        s.getAge(),
                        s.getGender()
                    });
            } catch (SQLException ex) { showErr(ex); }
        };
        refreshStudents.run();

        studentSearchBtn.addActionListener(e -> {
            String query = studentSearchField.getText().trim().toLowerCase();
            try {
                studentModel.setRowCount(0);
                int count = 0;
                for (Student s : studentDAO.getAll()) {
                    if (query.isEmpty() ||
                            String.valueOf(s.getId()).equalsIgnoreCase(query) ||
                            s.getName().toLowerCase().contains(query) ||
                            s.getAdmissionNumber().toLowerCase().contains(query)) {
                        studentModel.addRow(new Object[]{
                                s.getId(),
                                s.getAdmissionNumber(),
                                s.getName(),
                                s.getClassNo(),
                                s.getAge()
                        });
                        count++;
                    }
                }
                // If no records found, show message
                if (count == 0) {
                    JOptionPane.showMessageDialog(
                            studentPanel,
                            "No record found!",
                            "Search Result",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    studentSearchField.setText("");
                }
            } catch (SQLException ex) {
                showErr(ex);
            }
        });

        studentBackBtn.addActionListener(e -> {
            f.dispose();
            showLogin();
        });

        addStudent.addActionListener(e -> {
            StudentForm form = new StudentForm(null);
            int res = JOptionPane.showConfirmDialog(null, form.getPanel(), "Add Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    String username = form.getUsername();
                    String password = form.getPassword();
                    Student s = form.toStudent();
                    int sid = studentDAO.add(s);
                    // Only create user if both username and password are provided
                    if (!username.isEmpty() && !password.isEmpty()) {
                        if (userDAO.findByUsername(username) != null) {
                            JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.");
                            return;
                        }
                        userDAO.createUser(username, password, "STUDENT", sid);
                    }
                    refreshStudents.run();
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1 && studentTable.getSelectedRow() != -1) {
                    int row = studentTable.getSelectedRow();
                    int id = Integer.parseInt(studentModel.getValueAt(row, 0).toString());
                    try {
                        Student s = null;
                        for (Student st : studentDAO.getAll()) if (st.getId() == id) s = st;
                        if (s == null) return;
                        showStudentProfileDialog(s, refreshStudents);
                    } catch (SQLException ex) { showErr(ex); }
                }
            }
        });

        // --- Teachers Tab ---
        JPanel teacherPanel = new JPanel(new BorderLayout());

        JPanel teacherTopPanel = new JPanel();
        teacherTopPanel.setLayout(new BoxLayout(teacherTopPanel, BoxLayout.Y_AXIS));
        teacherTopPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel teacherSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField teacherSearchField = new JTextField(20);
        JButton teacherSearchBtn = new JButton("Search");
        teacherSearchPanel.add(new JLabel("Search Teacher by ID/Name:"));
        teacherSearchPanel.add(teacherSearchField);
        teacherSearchPanel.add(teacherSearchBtn);

        teacherTopPanel.add(teacherSearchPanel);

        JPanel teacherCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addTeacher = new JButton("Add Teacher");
        JButton editTeacher = new JButton("Update Teacher");
        JButton delTeacher = new JButton("Delete Teacher");
        teacherCtrl.add(addTeacher); teacherCtrl.add(editTeacher); teacherCtrl.add(delTeacher);
        teacherTopPanel.add(teacherCtrl);

        teacherPanel.add(teacherTopPanel, BorderLayout.NORTH);

        // Update table model to include Gender and Phone columns
        DefaultTableModel teacherModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Subject", "Qualification", "Class Assigned", "Gender", "Phone"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable teacherTable = new JTable(teacherModel);
        teacherTable.setRowHeight(28);
        teacherTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane teacherScroll = new JScrollPane(teacherTable);
        teacherPanel.add(teacherScroll, BorderLayout.CENTER);

        Runnable refreshTeachers = () -> {
            try {
                teacherModel.setRowCount(0);
                for (Teacher t : teacherDAO.getAll())
                    teacherModel.addRow(new Object[]{
                        t.getId(),
                        t.getName(),
                        t.getSubject(),
                        t.getQualification(),
                        t.getClassAssigned(),
                        t.getGender(),
                        t.getPhone_number() 
                    });
            } catch (SQLException ex) { showErr(ex); }
        };
        refreshTeachers.run();

        teacherSearchBtn.addActionListener(e -> {
            String query = teacherSearchField.getText().trim().toLowerCase();
            int matchingRecordsCount = 0;
            try{
                teacherModel.setRowCount(0);
                for (Teacher t : teacherDAO.getAll()) {
                    if (query.isEmpty() ||
                            String.valueOf(t.getId()).equalsIgnoreCase(query) ||
                            t.getName().toLowerCase().contains(query)) {
                        teacherModel.addRow(new Object[]{t.getId(), t.getName(), t.getSubject(), t.getQualification(), t.getClassAssigned()});
                        matchingRecordsCount++;
                    }
                }
                if (matchingRecordsCount == 0) {
                    teacherModel.addRow(new Object[]{ "No records found"});
                    teacherSearchField.setText("");
                }
            } catch (SQLException ex) {
                showErr(ex);
            }});
        addTeacher.addActionListener(e -> {
            TeacherForm form = new TeacherForm(null);
            int res = JOptionPane.showConfirmDialog(null, form.getPanel(), "Add Teacher", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    String username = form.getUsername();
                    String password = form.getPassword();
                    Teacher t = form.toTeacher();
                    int tid = teacherDAO.add(t);
                    // Only create user if both username and password are provided
                    if (!username.isEmpty() && !password.isEmpty()) {
                        if (userDAO.findByUsername(username) != null) {
                            JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.");
                            return;
                        }
                        userDAO.createUser(username, password, "TEACHER", tid);
                    }
                    refreshTeachers.run();
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        editTeacher.addActionListener(e -> {
            int r = teacherTable.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(null, "Select a teacher"); return; }
            try {
                int id = Integer.parseInt(teacherModel.getValueAt(r, 0).toString());
                Teacher t = null;
                for (Teacher tt : teacherDAO.getAll()) if (tt.getId() == id) t = tt;
                TeacherForm form = new TeacherForm(t);
                int res = JOptionPane.showConfirmDialog(null, form.getPanel(), "Update Teacher", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (res == JOptionPane.OK_OPTION) {
                    Teacher nt = form.toTeacher();
                    nt.setId(id);
                    teacherDAO.update(nt);
                    refreshTeachers.run();
                    JOptionPane.showMessageDialog(null, "Updated successfully.");
                }
            } catch (SQLException ex) { showErr(ex); }
        });

        delTeacher.addActionListener(e -> {
            int r = teacherTable.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(null, "Select a teacher"); return; }
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Are you sure you want to delete this teacher?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(teacherModel.getValueAt(r, 0).toString());
                    teacherDAO.delete(id);
                    refreshTeachers.run();
                    JOptionPane.showMessageDialog(null, "Record deleted.");
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        // --- Classes Tab ---
        JPanel classPanel = adminClassesPanel();

        tabs.addTab("Home", homePanel);
        tabs.addTab("Students", studentPanel);
        tabs.addTab("Teachers", teacherPanel);
        tabs.addTab("Classes", classPanel);

        f.add(tabs);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


    private JPanel adminClassesPanel(){
        JPanel p = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel(new String[]{"Class","No. of Students","Class Teacher"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton refreshBtn = new JButton("Refresh");
        JButton addClassBtn = new JButton("Add Class");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addClassBtn);
        btnPanel.add(refreshBtn);
        bottomPanel.add(btnPanel, BorderLayout.EAST);
        p.add(bottomPanel, BorderLayout.SOUTH);

        Runnable refresh = () -> {
            try {
                model.setRowCount(0);
                for (int cls = 1; cls <= 10; cls++){
                    int count = studentDAO.countByClass(cls);
                    String classTeacherName = teacherDAO.getNameByClass(cls);
                    if (classTeacherName == null || classTeacherName.trim().isEmpty()) {
                        classTeacherName = "Unassigned";
                    }
                    model.addRow(new Object[]{cls, count, classTeacherName});
                }
            } catch(SQLException ex){
                showErr(ex);
            }
        };

        refresh.run();
        refreshBtn.addActionListener(e -> refresh.run());

        // Show students in class on row click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    int classNo = Integer.parseInt(model.getValueAt(row, 0).toString());
                    try {
                        List<Student> students = studentDAO.getByClass(classNo);
                        if (students.isEmpty()) {
                            JOptionPane.showMessageDialog(p, "No students found in this class.", "Class Students", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        JDialog dialog = new JDialog((Frame) null, "Students in Class " + classNo, true);
                        DefaultTableModel studentModel = new DefaultTableModel(
                            new String[]{"ID", "Admission Number", "Name", "Age", "Address"}, 0
                        ) {
                            public boolean isCellEditable(int r, int c) { return false; }
                        };
                        for (Student s : students) {
                            studentModel.addRow(new Object[]{
                                s.getId(),
                                s.getAdmissionNumber(),
                                s.getName(),
                                s.getAge(),
                                s.getAddress()
                            });
                        }
                        JTable studentTable = new JTable(studentModel);
                        studentTable.setRowHeight(24);
                        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
                        JScrollPane scroll = new JScrollPane(studentTable);

                        dialog.getContentPane().add(scroll, BorderLayout.CENTER);
                        dialog.setSize(600, 400);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    } catch (SQLException ex) {
                        showErr(ex);
                    }
                }
            }
        });

        // Add class logic (simple example: prompt for class number and teacher name)
        addClassBtn.addActionListener(e -> {
            JPanel form = new JPanel(new GridLayout(2,2,5,5));
            JTextField classNoField = new JTextField();
            JTextField teacherNameField = new JTextField();
            form.add(new JLabel("Grade:")); form.add(classNoField);
            form.add(new JLabel("Class Teacher Name:")); form.add(teacherNameField);
            int res = JOptionPane.showConfirmDialog(p, form, "Add Class", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int classNo = Integer.parseInt(classNoField.getText().trim());
                    String teacherName = teacherNameField.getText().trim();
                    // You may need to implement addClass in your DAO, here is a simple placeholder:
                    teacherDAO.assignTeacherToClass(teacherName, classNo); 
                    refresh.run();
                    JOptionPane.showMessageDialog(p, "Class added/updated successfully.");
                } catch (Exception ex) {
                    showErr(ex);
                }
            }
        });

        return p;
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

        f.add(tabs);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


    // Enhanced teacherStudentsPanel with search and back button
    private JPanel teacherStudentsPanel(Teacher teacher, JFrame parentFrame){
        JPanel p = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton backBtn = new JButton("Logout");
        searchPanel.add(new JLabel("Search Student by ID/Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(backBtn);
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
                List<Student> list = studentDAO.getByClass(teacher.getClassAssigned());
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
                List<Student> list = studentDAO.getByClass(teacher.getClassAssigned());
                for (Student s : list) {
                    if (query.isEmpty() ||
                        String.valueOf(s.getId()).equalsIgnoreCase(query) ||
                        s.getName().toLowerCase().contains(query)) {
                        model.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getClassNo()});
                    }
                }
            } catch(SQLException ex){ showErr(ex); }
        });

        backBtn.addActionListener(e -> {
            parentFrame.dispose();
            showLogin();
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

        JButton backBtn = new JButton("Back");
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
            f.dispose();
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

    // Helper to parse marks string into a map: exam -> subject -> mark
    private java.util.Map<String, java.util.Map<String, String>> parseMarks(String marksStr) {
        java.util.Map<String, java.util.Map<String, String>> examMap = new java.util.HashMap<>();
        if (marksStr == null || marksStr.trim().isEmpty()) return examMap;
        // Example: "First Internal: Math:80, English:90, ...; Term Exam: Math:85, English:88, ..."
        String[] exams = marksStr.split(";");
        for (String examEntry : exams) {
            examEntry = examEntry.trim();
            if (examEntry.isEmpty()) continue;
            int idx = examEntry.indexOf(":");
            if (idx == -1) continue;
            String examName = examEntry.substring(0, idx).trim();
            String subjectsStr = examEntry.substring(idx + 1).trim();
            java.util.Map<String, String> subjectMap = new java.util.HashMap<>();
            for (String subjMark : subjectsStr.split(",")) {
                String[] pair = subjMark.split(":");
                if (pair.length == 2) subjectMap.put(pair[0].trim(), pair[1].trim());
            }
            examMap.put(examName, subjectMap);
        }
        return examMap;
    }

    // Helper to serialize marks map back to string
    private String serializeMarks(java.util.Map<String, java.util.Map<String, String>> examMap) {
        StringBuilder sb = new StringBuilder();
        for (String exam : examMap.keySet()) {
            sb.append(exam).append(": ");
            java.util.Map<String, String> subjectMap = examMap.get(exam);
            int i = 0;
            for (String subj : subjectMap.keySet()) {
                sb.append(subj).append(":").append(subjectMap.get(subj));
                if (++i < subjectMap.size()) sb.append(", ");
            }
            sb.append("; ");
        }
        return sb.toString().trim();
    }
}
