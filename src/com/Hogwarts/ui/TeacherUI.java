package com.Hogwarts.ui;

import com.Hogwarts.DAO.TeacherDAO;
import com.Hogwarts.DAO.UserDAO;
import com.Hogwarts.model.Teacher;
import com.Hogwarts.model.User;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class TeacherUI {
    private final TeacherDAO teacherDAO;
    private final UserDAO userDAO;

    // --- Enhanced UI Theme ---
    private static final Color PRIMARY = new Color(52, 73, 94);
    private static final Color SECONDARY = new Color(41, 128, 185);
    private static final Color ACCENT = new Color(46, 204, 113);
    private static final Color DANGER = new Color(231, 76, 60);
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

    public TeacherUI(TeacherDAO teacherDAO, UserDAO userDAO) {
        this.teacherDAO = teacherDAO;
        this.userDAO = userDAO;
    }

    public JPanel getPanel() {
    JPanel teacherPanel = new JPanel(new BorderLayout());
    teacherPanel.setBackground(BG);
    teacherPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top controls card
        JPanel topCard = createCard();
        topCard.setLayout(new BoxLayout(topCard, BoxLayout.Y_AXIS));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        JTextField teacherSearchField = new JTextField(25);
        teacherSearchField.setFont(LABEL_FONT);
        teacherSearchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JButton teacherSearchBtn = new JButton("Search");
        styleButton(teacherSearchBtn, SECONDARY);
        
        searchPanel.add(new JLabel("Search Teacher:"));
        searchPanel.add(teacherSearchField);
        searchPanel.add(teacherSearchBtn);
        topCard.add(searchPanel);

        JPanel teacherCtrl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        teacherCtrl.setOpaque(false);
    JButton addTeacher = new JButton("➕ Add Teacher");
    JButton editTeacher = new JButton("✏️ Update Teacher");
    JButton delTeacher = new JButton("🗑️ Delete Teacher");
    styleButton(addTeacher, ACCENT);
    styleButton(editTeacher, SECONDARY);
    styleButton(delTeacher, DANGER);
    teacherCtrl.add(addTeacher);
    teacherCtrl.add(editTeacher);
    teacherCtrl.add(delTeacher);
        topCard.add(teacherCtrl);

        teacherPanel.add(topCard, BorderLayout.NORTH);

        // Table in card
        DefaultTableModel teacherModel = new DefaultTableModel(
            new String[]{"ID", "Name","Gender", "Subject", "Qualification", "Class"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable teacherTable = new JTable(teacherModel);
    styleTable(teacherTable);
    
    JScrollPane scrollPane = new JScrollPane(teacherTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        teacherPanel.add(scrollPane, BorderLayout.CENTER);

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
                // Validate required fields before adding
                String validationError = form.validateFields();
                if (validationError != null) {
                    JOptionPane.showMessageDialog(teacherPanel, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
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
                } catch(Exception eo){
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
                
                // Get existing user credentials
                User existingUser = userDAO.findByRoleAndRefId("TEACHER", id);
                
                TeacherForm form = new TeacherForm(t);
                
                // Pre-populate username/password if exists
                if (existingUser != null) {
                    form.setUsername(existingUser.getUsername());
                    form.setPassword(existingUser.getPassword());
                }
                
                int res = JOptionPane.showConfirmDialog(null, form.getPanel(), "Update Teacher", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (res == JOptionPane.OK_OPTION) {
                    // Validate name field
                    String validationError = form.validateFields();
                    if (validationError != null) {
                        JOptionPane.showMessageDialog(teacherPanel, validationError, "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    Teacher nt = form.toTeacher();
                    nt.setId(id);
                    teacherDAO.update(nt);
                    
                    // Update or create user credentials if provided
                    String newUsername = form.getUsername();
                    String newPassword = form.getPassword();
                    
                    if (!newUsername.isEmpty() && !newPassword.isEmpty()) {
                        // Check if username is taken by another user
                        User userCheck = userDAO.findByUsername(newUsername);
                        if (userCheck != null && (userCheck.getRefId() == null || userCheck.getRefId() != id || !userCheck.getRole().equals("TEACHER"))) {
                            JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.");
                            return;
                        }
                        
                        if (existingUser != null) {
                            // Update existing user
                            boolean updated = userDAO.updateUserCredentials("TEACHER", id, newUsername, newPassword);
                            if (updated) {
                                JOptionPane.showMessageDialog(null, "Teacher and login credentials updated successfully.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Teacher updated, but failed to update login credentials.");
                            }
                        } else {
                            // Create new user
                            boolean created = userDAO.createUser(newUsername, newPassword, "TEACHER", id);
                            if (created) {
                                JOptionPane.showMessageDialog(null, "Teacher updated and login credentials created successfully.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Teacher updated, but failed to create login credentials.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Teacher updated successfully.");
                    }
                    
                    refreshTeachers.run();
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
            panel = new JPanel(new BorderLayout(0,8));
            JLabel hdr = new JLabel(t==null ? "Add Teacher" : "Update Teacher", JLabel.CENTER);
            hdr.setFont(new Font("Segoe UI", Font.BOLD, 16));
            hdr.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
            panel.add(hdr, BorderLayout.NORTH);

            JPanel content = new JPanel(new GridBagLayout());
            content.setBackground(CARD_BG);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,8,6,8);
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
            styleField.accept(nameField); styleField.accept(subjectField); styleField.accept(phoneField);
            styleField.accept(qualField); styleField.accept(usernameField); styleField.accept(passwordField);

            content.add(new JLabel("Name:"), gbc);
            gbc.gridx = 1; content.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Subject:"), gbc);
            gbc.gridx = 1; content.add(subjectField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Phone Number:"), gbc);
            gbc.gridx = 1; content.add(phoneField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Qualification:"), gbc);
            gbc.gridx = 1; content.add(qualField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Class Assigned (optional):"), gbc);
            classBox.addItem(null); for(int i=1;i<=10;i++) classBox.addItem(i);
            gbc.gridx = 1; content.add(classBox, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Gender:"), gbc);
            JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); genderPanel.setOpaque(false);
            genderGroup.add(maleBtn); genderGroup.add(femaleBtn); genderPanel.add(maleBtn); genderPanel.add(femaleBtn);
            gbc.gridx = 1; content.add(genderPanel, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Login username:"), gbc);
            gbc.gridx = 1; content.add(usernameField, gbc);
            gbc.gridx = 0; gbc.gridy++;
            content.add(new JLabel("Login password:"), gbc);
            gbc.gridx = 1; content.add(passwordField, gbc);

            panel.add(content, BorderLayout.CENTER);

            if (t!=null){
                nameField.setText(t.getName());
                subjectField.setText(t.getSubject());
                qualField.setText(t.getQualification());
                if (t.getClassAssigned()!=null) classBox.setSelectedItem(t.getClassAssigned());
                if ("Male".equalsIgnoreCase(t.getGender())) maleBtn.setSelected(true);
                else if ("Female".equalsIgnoreCase(t.getGender())) femaleBtn.setSelected(true);
            } else { maleBtn.setSelected(true); }
        }
        public JPanel getPanel(){ return panel; }
        // Validate required fields. Returns null when OK, otherwise error message.
        public String validateFields() {
            String nm = nameField.getText() == null ? "" : nameField.getText().trim();
            if (nm.isEmpty()) return "Name is required.";
            return null;
        }
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
        // Add setters to pre-populate form when editing
        public void setUsername(String username) { if (username != null) usernameField.setText(username); }
        public void setPassword(String password) { if (password != null) passwordField.setText(password); }
     }

    private void showErr(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); }
}
