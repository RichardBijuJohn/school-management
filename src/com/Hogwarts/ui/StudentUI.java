package com.Hogwarts.ui;

import com.Hogwarts.DAO.StudentDAO;
import com.Hogwarts.DAO.UserDAO;
import com.Hogwarts.model.Student;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StudentUI {
    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    public StudentUI(StudentDAO studentDAO, UserDAO userDAO) {
        this.studentDAO = studentDAO;
        this.userDAO = userDAO;
    }

    public JPanel getPanel() {
        JPanel studentPanel = new JPanel(new BorderLayout());
        studentPanel.setBackground(new Color(245, 245, 220)); // beige

        JPanel studentTopPanel = new JPanel();
        studentTopPanel.setLayout(new BoxLayout(studentTopPanel, BoxLayout.Y_AXIS));
        studentTopPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        studentTopPanel.setBackground(new Color(245, 245, 220)); // beige

        JPanel studentSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentSearchPanel.setBackground(new Color(245, 245, 220)); // beige
        JTextField studentSearchField = new JTextField(20);
        JButton studentSearchBtn = new JButton("Search");

        studentSearchPanel.add(new JLabel("Search Student by Adm_No/Name:"));
        studentSearchPanel.add(studentSearchField);
        studentSearchPanel.add(studentSearchBtn);

        studentTopPanel.add(studentSearchPanel);

        JPanel studentCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        studentCtrl.setBackground(new Color(245, 245, 220)); // beige
        JButton addStudent = new JButton("Add Student");
        studentCtrl.add(addStudent);
        studentTopPanel.add(studentCtrl);

        studentPanel.add(studentTopPanel, BorderLayout.NORTH);

        DefaultTableModel studentModel = new DefaultTableModel(
                new String[]{"ID","Admission_Number", "Name","Gender", "Age","Class"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable studentTable = new JTable(studentModel);
        studentTable.setBackground(new Color(245, 245, 220)); // beige
        studentTable.setRowHeight(28);
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
        try {
            String username = form.getUsername();
            String password = form.getPassword();
            Student s = form.toStudent();
            int sid = studentDAO.add(s);
            JOptionPane.showMessageDialog(null, "Student added successfully.");
            // Ensure user is created after student is added
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

    private void showErr(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); }
}
