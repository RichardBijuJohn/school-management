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
    private UserDAO userDAO = new UserDAO();
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
                if (user==null) { JOptionPane.showMessageDialog(frame,"Invalid credentials"); return; }
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
    private void showAdminPanel(){
        JFrame f = new JFrame("Admin Panel");
        f.setSize(1000, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();

        // --- Students Tab ---
        JPanel studentPanel = new JPanel(new BorderLayout());

        // Use BoxLayout for vertical stacking
        JPanel studentTopPanel = new JPanel();
        studentTopPanel.setLayout(new BoxLayout(studentTopPanel, BoxLayout.Y_AXIS));
        studentTopPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel studentSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField studentSearchField = new JTextField(20);
        JButton studentSearchBtn = new JButton("Search");
        JButton studentBackBtn = new JButton("Back");
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

        DefaultTableModel studentModel = new DefaultTableModel(
            new String[]{"ID","Admission_Number", "Name", "Class", "Age"}, 0
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
                    studentModel.addRow(new Object[]{s.getId(), s.getAdmissionNumber(), s.getName(), s.getClassNo(), s.getAge()});
            } catch (SQLException ex) { showErr(ex); }
        };
        refreshStudents.run();

        studentSearchBtn.addActionListener(e -> {
            String query = studentSearchField.getText().trim().toLowerCase();
            try {
                studentModel.setRowCount(0);
                for (Student s : studentDAO.getAll()) {
                    if (query.isEmpty() ||
                        String.valueOf(s.getId()).equalsIgnoreCase(query) ||
                        s.getName().toLowerCase().contains(query) ||
                        s.getAdmissionNumber().toLowerCase().contains(query)) {
                        studentModel.addRow(new Object[]{s.getId(), s.getAdmissionNumber(), s.getName(), s.getClassNo(), s.getAge()});
                    }
                }
            } catch (SQLException ex) { showErr(ex); }
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
                    Student s = form.toStudent();
                    int sid = studentDAO.add(s); // create login for student
                    userDAO.createUser(form.usernameField.getText().trim(), form.passwordField.getText().trim(), "STUDENT", sid);
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
        JButton editTeacher = new JButton("Edit Teacher");
        JButton delTeacher = new JButton("Delete Teacher");
        teacherCtrl.add(addTeacher); teacherCtrl.add(editTeacher); teacherCtrl.add(delTeacher);
        teacherTopPanel.add(teacherCtrl);

        teacherPanel.add(teacherTopPanel, BorderLayout.NORTH);

        DefaultTableModel teacherModel = new DefaultTableModel(
            new String[]{"ID", "Name", "Subject", "Qualification", "Class Assigned"}, 0
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
                    teacherModel.addRow(new Object[]{t.getId(), t.getName(), t.getSubject(), t.getQualification(), t.getClassAssigned()});
            } catch (SQLException ex) { showErr(ex); }
        };
        refreshTeachers.run();

        teacherSearchBtn.addActionListener(e -> {
            String query = teacherSearchField.getText().trim().toLowerCase();
            try {
                teacherModel.setRowCount(0);
                for (Teacher t : teacherDAO.getAll()) {
                    if (query.isEmpty() ||
                        String.valueOf(t.getId()).equalsIgnoreCase(query) ||
                        t.getName().toLowerCase().contains(query)) {
                        teacherModel.addRow(new Object[]{t.getId(), t.getName(), t.getSubject(), t.getQualification(), t.getClassAssigned()});
                    }
                }
            } catch (SQLException ex) { showErr(ex); }
        });

        addTeacher.addActionListener(e -> {
            TeacherForm form = new TeacherForm(null);
            int res = JOptionPane.showConfirmDialog(null, form.getPanel(), "Add Teacher", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    Teacher t = form.toTeacher();
                    int tid = teacherDAO.add(t); // create login for teacher
                    userDAO.createUser(form.usernameField.getText().trim(), form.passwordField.getText().trim(), "TEACHER", tid);
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
                int res = JOptionPane.showConfirmDialog(null, form.getPanel(), "Edit Teacher", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (res == JOptionPane.OK_OPTION) {
                    Teacher nt = form.toTeacher();
                    nt.setId(id);
                    teacherDAO.update(nt);
                    refreshTeachers.run();
                }
            } catch (SQLException ex) { showErr(ex); }
        });

        delTeacher.addActionListener(e -> {
            int r = teacherTable.getSelectedRow();
            if (r == -1) { JOptionPane.showMessageDialog(null, "Select a teacher"); return; }
            try {
                int id = Integer.parseInt(teacherModel.getValueAt(r, 0).toString());
                teacherDAO.delete(id);
                refreshTeachers.run();
            } catch (SQLException ex) { showErr(ex); }
        });

        // --- Classes Tab ---
        JPanel classPanel = adminClassesPanel();

        tabs.addTab("Students", studentPanel);
        tabs.addTab("Teachers", teacherPanel);
        tabs.addTab("Classes", classPanel);

        f.add(tabs);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }


    private JPanel adminClassesPanel(){
        JPanel p = new JPanel(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(new String[]{"Class","No. of Students","Students (Names)"},0){ public boolean isCellEditable(int r,int c){return false;} };
        JTable table = new JTable(model);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton refreshBtn = new JButton("Refresh"); p.add(refreshBtn, BorderLayout.SOUTH);


        Runnable refresh = () -> { try { model.setRowCount(0); for (int cls=1; cls<=10; cls++){ int count = studentDAO.countByClass(cls); List<Student> s = studentDAO.getByClass(cls); String names = ""; for (Student st: s) names += st.getName() + ", "; if (names.endsWith(", ")) names = names.substring(0, names.length()-2); model.addRow(new Object[]{cls, count, names}); } } catch(SQLException ex){ showErr(ex);} };
        refresh.run();
        refreshBtn.addActionListener(e -> refresh.run());
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
        JButton backBtn = new JButton("Back");
        searchPanel.add(new JLabel("Search Student by ID/Name:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(backBtn);
        p.add(searchPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Age","Class","Address","Marks"}, 0
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
                    JOptionPane.showMessageDialog(null,"No class assigned to you."); return;
                }
                List<Student> list = studentDAO.getByClass(teacher.getClassAssigned());
                for (Student s: list)
                    model.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getClassNo(), s.getAddress(), s.getMarks()});
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
                        model.addRow(new Object[]{s.getId(), s.getName(), s.getAge(), s.getClassNo(), s.getAddress(), s.getMarks()});
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
            if (r==-1){ JOptionPane.showMessageDialog(null,"Select a student"); return; }
            int id = Integer.parseInt(model.getValueAt(r,0).toString());
            String currentMarks = model.getValueAt(r,5).toString();
            String nv = JOptionPane.showInputDialog(null, "Enter marks (simple text, e.g. Math:80,Eng:75):", currentMarks);
            if (nv!=null){
                try {
                    Student s = null;
                    for (Student st: studentDAO.getByClass(teacher.getClassAssigned())) if (st.getId()==id) s=st;
                    if (s==null){ JOptionPane.showMessageDialog(null,"You can only edit students of your class."); return; }
                    s.setMarks(nv); studentDAO.update(s); refresh.run();
                } catch(SQLException ex){ showErr(ex); }
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
        private JTextField subjectField = new JTextField(20);
        private JTextField qualField = new JTextField(20);
        private JComboBox<Integer> classBox = new JComboBox<>();
        private JTextField usernameField = new JTextField(12);
        private JTextField passwordField = new JTextField(12);
        public TeacherForm(Teacher t){
            panel = new JPanel(new GridLayout(6,2,5,5));
            panel.add(new JLabel("Name:")); panel.add(nameField);
            panel.add(new JLabel("Subject:")); panel.add(subjectField);
            panel.add(new JLabel("Qualification:")); panel.add(qualField);
            panel.add(new JLabel("Class Assigned (1-10):")); for(int i=1;i<=10;i++) classBox.addItem(i); panel.add(classBox);
            panel.add(new JLabel("Login username:")); panel.add(usernameField);
            panel.add(new JLabel("Login password:")); panel.add(passwordField);
            if (t!=null){ nameField.setText(t.getName()); subjectField.setText(t.getSubject()); qualField.setText(t.getQualification()); if (t.getClassAssigned()!=null) classBox.setSelectedItem(t.getClassAssigned()); }
        }
        public JPanel getPanel(){ return panel; }
        public Teacher toTeacher(){ return new Teacher(nameField.getText().trim(), subjectField.getText().trim(), qualField.getText().trim(), (Integer)classBox.getSelectedItem()); }
    }


    // StudentForm used in admin to add/edit student + create login credentials
    class StudentForm {
        private JPanel panel;
        private JTextField admissionNumberField = new JTextField();

        private JTextField nameField = new JTextField(20);
        private JTextField ageField = new JTextField(4);
        private JComboBox<Integer> classBox = new JComboBox<>();
        private JTextField addressField = new JTextField(30);
        private JTextField marksField = new JTextField(30);
        private JTextField fatherNameField = new JTextField(20);
        private JTextField fatherNumberField = new JTextField(20);
        private JTextField dobField = new JTextField(12);
        private JTextField usernameField = new JTextField(12);
        private JTextField passwordField = new JTextField(12);
        public StudentForm(Student s){
            panel = new JPanel(new GridLayout(10,2,5,5));
            panel.add(new JLabel("AdmissionNumber:"));panel.add(admissionNumberField);
            panel.add(new JLabel("Name:")); panel.add(nameField);
            panel.add(new JLabel("Age:")); panel.add(ageField);
            panel.add(new JLabel("Class (1-10):")); for(int i=1;i<=10;i++) classBox.addItem(i); panel.add(classBox);
            panel.add(new JLabel("Address:")); panel.add(addressField);
            panel.add(new JLabel("Father's Name:")); panel.add(fatherNameField);
            panel.add(new JLabel("Father's Number:")); panel.add(fatherNumberField);
            panel.add(new JLabel("DOB:")); panel.add(dobField);
            panel.add(new JLabel("Marks (e.g. Math:0,Eng:0):")); panel.add(marksField);
            panel.add(new JLabel("Login username:")); panel.add(usernameField);
            panel.add(new JLabel("Login password:")); panel.add(passwordField);
            if (s!=null){
                admissionNumberField.setText(s.getAdmissionNumber());
                nameField.setText(s.getName());
                ageField.setText(String.valueOf(s.getAge()));
                classBox.setSelectedItem(s.getClassNo());
                addressField.setText(s.getAddress());
                marksField.setText(s.getMarks());
                fatherNameField.setText(s.getFatherName());
                fatherNumberField.setText(s.getFatherNumber());
                dobField.setText(s.getDob());
            }
        }
        public JPanel getPanel(){ return panel; }
        public Student toStudent(){
            int age=0; try{ age=Integer.parseInt(ageField.getText().trim()); } catch(Exception e){}
            return new Student(
                admissionNumberField.getText().trim(),
                nameField.getText().trim(),
                age,
                (Integer)classBox.getSelectedItem(),
                addressField.getText().trim(),
                marksField.getText().trim(),
                fatherNameField.getText().trim(),
                fatherNumberField.getText().trim(),
                dobField.getText().trim()
            );
        }
    }


    // ----------------- Utility -----------------
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
        panel.add(new JLabel("Address:")); panel.add(new JLabel(s.getAddress()));
        panel.add(new JLabel("Father's Name:")); panel.add(new JLabel(s.getFatherName()));
        panel.add(new JLabel("Father's Number:")); panel.add(new JLabel(s.getFatherNumber()));
        panel.add(new JLabel("DOB:")); panel.add(new JLabel(s.getDob()));
        panel.add(new JLabel("Marks:")); panel.add(new JLabel(s.getMarks()));

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
                    dialog.dispose();
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Are you sure you want to delete this student?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    studentDAO.delete(s.getId());
                    refreshStudents.run();
                    dialog.dispose();
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        dialog.setVisible(true);
    }
}