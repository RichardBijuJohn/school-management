package com.Hogwarts.ui;

import com.Hogwarts.DAO.StudentDAO;
import com.Hogwarts.DAO.UserDAO;
import com.Hogwarts.model.Student;
import com.Hogwarts.model.User;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class StudentUI {
    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    // --- Enhanced UI Theme (same as TeacherUI) ---
    private static final Color PRIMARY = new Color(52, 73, 94);
    private static final Color SECONDARY = new Color(41, 128, 185);
    private static final Color ACCENT = new Color(46, 204, 113);
    private static final Color BG = new Color(236, 240, 241);
    private static final Color CARD_BG = Color.WHITE;
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private static void styleButton(AbstractButton b, Color bgColor) {
        b.setBackground(bgColor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(BUTTON_FONT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
    }

    private static void styleTable(JTable t) {
        t.setFont(LABEL_FONT);
        t.setRowHeight(32);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(52, 152, 219));
        t.setSelectionForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.getTableHeader().setBackground(PRIMARY);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setReorderingAllowed(false);
        t.setFillsViewportHeight(true);
    }
    
    private static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }
    // ---

    public StudentUI(StudentDAO studentDAO, UserDAO userDAO) {
        this.studentDAO = studentDAO;
        this.userDAO = userDAO;
    }

    public JPanel getPanel() {
    JPanel studentPanel = new JPanel(new BorderLayout());
    studentPanel.setBackground(BG);
    studentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top controls card
        JPanel topCard = createCard();
        topCard.setLayout(new BoxLayout(topCard, BoxLayout.Y_AXIS));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        JTextField studentSearchField = new JTextField(25);
        studentSearchField.setFont(LABEL_FONT);
        studentSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JButton studentSearchBtn = new JButton("Search");
        styleButton(studentSearchBtn, SECONDARY);

        searchPanel.add(new JLabel("Search Student:"));
        searchPanel.add(studentSearchField);
        searchPanel.add(studentSearchBtn);
        topCard.add(searchPanel);

    JPanel studentCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
    studentCtrl.setOpaque(false);
    JButton addStudent = new JButton("➕ Add Student");
    styleButton(addStudent, ACCENT);
    studentCtrl.add(addStudent);
        topCard.add(studentCtrl);

        studentPanel.add(topCard, BorderLayout.NORTH);

        // Table
        DefaultTableModel studentModel = new DefaultTableModel(
                new String[]{"ID","Admission No.", "Name","Gender", "Age","Class"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
    JTable studentTable = new JTable(studentModel);
    styleTable(studentTable);
    
    JScrollPane scrollPane = new JScrollPane(studentTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        studentPanel.add(scrollPane, BorderLayout.CENTER);

        Runnable refreshStudents = () -> {
            try {
                studentModel.setRowCount(0);
                for (Student s : studentDAO.getAll())
                    studentModel.addRow(new Object[]{
                        s.getId(),
                        s.getAdmissionNumber(),
                        s.getName(),
                        s.getGender(),
                            s.getAge(),
                        s.getClassNo()

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
                                s.getGender(),
                                s.getClassNo(),
                                s.getAge()
                        });
                        count++;
                    }
                }
                if (count == 0) {
                    JOptionPane.showMessageDialog(studentPanel, "No record found!", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    studentSearchField.setText("");
                }
            } catch (SQLException ex) { showErr(ex); }
        });



    addStudent.addActionListener(e -> {
    StudentForm form = new StudentForm(null);
    int res = JOptionPane.showConfirmDialog(null, form.getPanel(), "Add Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    if (res == JOptionPane.OK_OPTION) {
        // Validate mandatory fields before adding
        String validationError = form.validateFields();
        if (validationError != null) {
            JOptionPane.showMessageDialog(studentPanel, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String username = form.getUsername();
            String password = form.getPassword();
            Student s = form.toStudent();
            int sid = studentDAO.add(s);
            JOptionPane.showMessageDialog(null, "Student added successfully.");
            
            // Create user credentials if provided
            if (!username.isEmpty() && !password.isEmpty()) {
                if (userDAO.findByUsername(username) != null) {
                    JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.");
                    return;
                }
                boolean created = userDAO.createUser(username, password, "STUDENT", sid);
                if (!created) {
                    JOptionPane.showMessageDialog(null, "Failed to create login for student.");
                } else {
                    JOptionPane.showMessageDialog(null, "Student login created successfully.");
                }
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

        return studentPanel;
    }

    // student profile dialog and form (moved here)
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
            // Get existing user credentials
            User existingUser = null;
            try {
                existingUser = userDAO.findByRoleAndRefId("STUDENT", s.getId());
            } catch (SQLException ex) { showErr(ex); }
            
            StudentForm form = new StudentForm(s);
            
            // Pre-populate username/password if exists
            if (existingUser != null) {
                form.setUsername(existingUser.getUsername());
                form.setPassword(existingUser.getPassword());
            }
            
            int r = JOptionPane.showConfirmDialog(dialog, form.getPanel(), "Update Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r == JOptionPane.OK_OPTION) {
                // Validate required fields
                String validationError = form.validateFields();
                if (validationError != null) {
                    JOptionPane.showMessageDialog(dialog, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    Student ns = form.toStudent();
                    ns.setId(s.getId());
                    studentDAO.update(ns);
                    
                    // Update or create user credentials if provided
                    String newUsername = form.getUsername();
                    String newPassword = form.getPassword();
                    
                    if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                        // Check if username is taken by another user
                        User userCheck = userDAO.findByUsername(newUsername);
                        if (userCheck != null && (userCheck.getRefId() == null || userCheck.getRefId() != s.getId() || !userCheck.getRole().equals("STUDENT"))) {
                            JOptionPane.showMessageDialog(dialog, "Username already exists. Please choose another.");
                            return;
                        }
                        
                        if (existingUser != null) {
                            // Update existing user
                            boolean updated = userDAO.updateUserCredentials("STUDENT", s.getId(), newUsername, newPassword);
                            if (updated) {
                                JOptionPane.showMessageDialog(dialog, "Student and login credentials updated successfully.");
                            } else {
                                JOptionPane.showMessageDialog(dialog, "Student updated, but failed to update login credentials.");
                            }
                        } else {
                            // Create new user
                            boolean created = userDAO.createUser(newUsername, newPassword, "STUDENT", s.getId());
                            if (created) {
                                JOptionPane.showMessageDialog(dialog, "Student updated and login credentials created successfully.");
                            } else {
                                JOptionPane.showMessageDialog(dialog, "Student updated, but failed to create login credentials.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Student updated successfully.");
                    }
                    
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
                    JOptionPane.showMessageDialog(dialog, "Record deleted.");
                    dialog.dispose();
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        dialog.setVisible(true);
    }

    // Small StudentForm (copied/adapted)
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
            panel = new JPanel(new BorderLayout(0,8));
            JLabel hdr = new JLabel(s==null ? "Add Student" : "Update Student", JLabel.CENTER);
            hdr.setFont(new Font("Segoe UI", Font.BOLD, 16));
            hdr.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
            panel.add(hdr, BorderLayout.NORTH);

            JPanel content = new JPanel(new GridBagLayout());
            content.setBackground(CARD_BG);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 8, 6, 8);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0; gbc.gridy = 0;

            java.util.function.Consumer<JTextField> styleField = f -> {
                f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189,195,199),1),
                    new EmptyBorder(6,8,6,8)
                ));
                f.setFont(LABEL_FONT);
            };
            styleField.accept(admissionNumberField); styleField.accept(nameField); styleField.accept(ageField);
            styleField.accept(addressField); styleField.accept(firstInternalField); styleField.accept(secondInternalField);
            styleField.accept(termExamField); styleField.accept(fatherNameField); styleField.accept(fatherNumberField);
            styleField.accept(dobField); styleField.accept(usernameField); styleField.accept(passwordField);

            content.add(new JLabel("Admission Number:"), gbc);
            gbc.gridx = 1; content.add(admissionNumberField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1; content.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Age:"), gbc);
            gbc.gridx = 1; content.add(ageField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Class:"), gbc);
            gbc.gridx = 1; content.add(classBox, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Gender:"), gbc);
            gbc.gridx = 1;
            JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); genderPanel.setOpaque(false);
            genderGroup.add(maleBtn); genderGroup.add(femaleBtn); genderPanel.add(maleBtn); genderPanel.add(femaleBtn);
            content.add(genderPanel, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Address:"), gbc);
            gbc.gridx = 1; content.add(addressField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("First Internal:"), gbc);
            gbc.gridx = 1; content.add(firstInternalField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Second Internal:"), gbc);
            gbc.gridx = 1; content.add(secondInternalField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Term Exam:"), gbc);
            gbc.gridx = 1; content.add(termExamField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Father's Name:"), gbc);
            gbc.gridx = 1; content.add(fatherNameField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Father's Number:"), gbc);
            gbc.gridx = 1; content.add(fatherNumberField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("DOB:"), gbc);
            gbc.gridx = 1; content.add(dobField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Login username:"), gbc);
            gbc.gridx = 1; content.add(usernameField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Login password:"), gbc);
            gbc.gridx = 1; content.add(passwordField, gbc);

            panel.add(content, BorderLayout.CENTER);

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
        
        // Validate required fields. Returns null when OK, otherwise error message.
        public String validateFields() {
            String adm = admissionNumberField.getText() == null ? "" : admissionNumberField.getText().trim();
            String nm = nameField.getText() == null ? "" : nameField.getText().trim();
            if (adm.isEmpty() && nm.isEmpty()) return "Admission Number and Name are required.";
            if (adm.isEmpty()) return "Admission Number is required.";
            if (nm.isEmpty()) return "Name is required.";
            return null;
        }
        
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
        
        // Add setters to pre-populate form when editing
        public void setUsername(String username) { if (username != null) usernameField.setText(username); }
        public void setPassword(String password) { if (password != null) passwordField.setText(password); }
     }

    private void showErr(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); }
}
