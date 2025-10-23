package com.Hogwarts.ui;

import com.Hogwarts.DAO.TeacherDAO;
import com.Hogwarts.DAO.UserDAO;
import com.Hogwarts.model.Teacher;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TeacherUI {
    private final TeacherDAO teacherDAO;
    private final UserDAO userDAO;

    public TeacherUI(TeacherDAO teacherDAO, UserDAO userDAO) {
        this.teacherDAO = teacherDAO;
        this.userDAO = userDAO;
    }

    public JPanel getPanel() {
        JPanel teacherPanel = new JPanel(new BorderLayout());
        teacherPanel.setBackground(new Color(245, 245, 220)); // beige

        JPanel teacherTopPanel = new JPanel();
        teacherTopPanel.setLayout(new BoxLayout(teacherTopPanel, BoxLayout.Y_AXIS));
        teacherTopPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        teacherTopPanel.setBackground(new Color(245, 245, 220)); // beige

        JPanel teacherSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        teacherSearchPanel.setBackground(new Color(245, 245, 220)); // beige
        JTextField teacherSearchField = new JTextField(20);
        JButton teacherSearchBtn = new JButton("Search");
        teacherSearchPanel.add(new JLabel("Search Teacher by ID/Name:"));
        teacherSearchPanel.add(teacherSearchField);
        teacherSearchPanel.add(teacherSearchBtn);
        teacherTopPanel.add(teacherSearchPanel);

        JPanel teacherCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        teacherCtrl.setBackground(new Color(245, 245, 220)); // beige
        JButton addTeacher = new JButton("Add Teacher");
        JButton editTeacher = new JButton("Update Teacher");
        JButton delTeacher = new JButton("Delete Teacher");
        addTeacher.setFont(new Font("SansSerif", Font.BOLD, 13));
        editTeacher.setFont(new Font("SansSerif", Font.BOLD, 13));
        delTeacher.setFont(new Font("SansSerif", Font.BOLD, 13));
        teacherCtrl.add(addTeacher); teacherCtrl.add(editTeacher); teacherCtrl.add(delTeacher);
        teacherTopPanel.add(teacherCtrl);

        teacherPanel.add(teacherTopPanel, BorderLayout.NORTH);

        DefaultTableModel teacherModel = new DefaultTableModel(
            new String[]{"ID", "Name","Gender", "Subject", "Qualification", "Class Assigned"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable teacherTable = new JTable(teacherModel);
        teacherTable.setRowHeight(28);
        teacherTable.setBackground(new Color(245, 245, 220)); // beige
        teacherPanel.add(new JScrollPane(teacherTable), BorderLayout.CENTER);

        Runnable refreshTeachers = () -> {
            try {
                teacherModel.setRowCount(0);
                for (Teacher t : teacherDAO.getAll())
                    teacherModel.addRow(new Object[]{
                            t.getId(),
                            t.getName(),
                            t.getGender(),
                            t.getSubject(),
                            t.getQualification(),
                            t.getClassAssigned()});
            } catch (SQLException ex) { showErr(ex); }
        };
        refreshTeachers.run();

        teacherSearchBtn.addActionListener(e -> {
            String query = teacherSearchField.getText().trim().toLowerCase();
            try {
                teacherModel.setRowCount(0);
                int count = 0;
                for (Teacher t : teacherDAO.getAll()) {
                    if (query.isEmpty() ||
                            String.valueOf(t.getId()).equalsIgnoreCase(query) ||
                            t.getName().toLowerCase().contains(query)) {
                        teacherModel.addRow(new Object[]{
                                t.getId(),
                                t.getName(),
                                t.getGender(),
                                t.getSubject(),
                                t.getQualification(),
                                t.getClassAssigned()});
                        count++;
                    }
                }
                if (count == 0) {
                    teacherModel.addRow(new Object[]{ "No records found"});
                    teacherSearchField.setText("");
                }
            } catch (SQLException ex) { showErr(ex); }
        });

        addTeacher.addActionListener(e -> {
            TeacherForm form = new TeacherForm(null);
            int res = JOptionPane.showConfirmDialog(null, form.getPanel(), "Add Teacher", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    String username = form.getUsername();
                    String password = form.getPassword();
                    Teacher t = form.toTeacher();
                    // Set username and password in Teacher object for reference
                    t.setUsername(username);
                    t.setPassword(password);
                    int tid = teacherDAO.add(t);
                    JOptionPane.showMessageDialog(null, "Teacher added successfully.");
                    // Ensure user is created after teacher is added
                    if (!username.isEmpty() && !password.isEmpty()) {
                        if (userDAO.findByUsername(username) != null) {
                            JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.");
                            return;
                        }
                        boolean created = userDAO.createUser(t.getUsername(), t.getPassword(), "TEACHER", tid);
                        if (!created) {
                            JOptionPane.showMessageDialog(null, "Failed to create login for teacher.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Teacher login created successfully.");
                        }
                    }
                    refreshTeachers.run();
                } catch (SQLException ex) {
                    showErr(ex);
                    System.out.println("error:"+ex);
                }
                catch(Exception eo){
                    System.out.println("error:"+eo);
                }
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
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this teacher?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    int id = Integer.parseInt(teacherModel.getValueAt(r, 0).toString());
                    teacherDAO.delete(id);
                    refreshTeachers.run();
                    JOptionPane.showMessageDialog(null, "Record deleted.");
                } catch (SQLException ex) { showErr(ex); }
            }
        });

        return teacherPanel;
    }

    // Small TeacherForm
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
            panel.setBackground(new Color(224, 255, 255)); // light cyan
            panel.add(new JLabel("Name:")); panel.add(nameField);
            panel.add(new JLabel("Subject:")); panel.add(subjectField);
            panel.add(new JLabel("Phone Number:")); panel.add(phoneField);
            panel.add(new JLabel("Qualification:")); panel.add(qualField);
            panel.add(new JLabel("Class Assigned (optional):"));
            classBox.addItem(null); for(int i=1;i<=10;i++) classBox.addItem(i); panel.add(classBox);
            panel.add(new JLabel("Gender:"));
            JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            genderPanel.setBackground(new Color(224, 255, 255)); // light cyan
            genderGroup.add(maleBtn); genderGroup.add(femaleBtn);
            genderPanel.add(maleBtn); genderPanel.add(femaleBtn);
            panel.add(genderPanel);
            panel.add(new JLabel("Login username:")); panel.add(usernameField);
            panel.add(new JLabel("Login password:")); panel.add(passwordField);
            if (t!=null){
                nameField.setText(t.getName());
                subjectField.setText(t.getSubject());
                qualField.setText(t.getQualification());
                if (t.getClassAssigned()!=null) classBox.setSelectedItem(t.getClassAssigned());
                else classBox.setSelectedItem(null);
                if ("Male".equalsIgnoreCase(t.getGender())) maleBtn.setSelected(true);
                else if ("Female".equalsIgnoreCase(t.getGender())) femaleBtn.setSelected(true);
            } else {
                maleBtn.setSelected(true);
            }
        }
        public JPanel getPanel(){ return panel; }
        public Teacher toTeacher(){
            Integer classAssigned = (Integer)classBox.getSelectedItem();
            String gender = maleBtn.isSelected() ? "Male" : "Female";
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

    private void showErr(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); }
}
