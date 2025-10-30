package com.Hogwarts.main;

import com.Hogwarts.DAO.*;
import com.Hogwarts.model.*;
import com.Hogwarts.ui.ClassUI;
import com.Hogwarts.ui.StudentUI;
import com.Hogwarts.ui.TeacherUI;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Main {
    private JFrame frame;
    private final UserDAO userDAO = new UserDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private TeacherDAO teacherDAO = new TeacherDAO();

    // --- Enhanced UI Theme ---
    private static final Color PRIMARY = new Color(52, 73, 94);      // Dark blue-grey
    private static final Color SECONDARY = new Color(41, 128, 185);  // Bright blue
    private static final Color ACCENT = new Color(46, 204, 113);     // Green
    private static final Color BG = new Color(236, 240, 241);        // Light grey
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private static void styleButton(AbstractButton b) {
        b.setBackground(SECONDARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(BUTTON_FONT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY, 1, true),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(31, 97, 141));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(SECONDARY);
            }
        });
    }
    
    private static void styleAccentButton(AbstractButton b) {
        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(BUTTON_FONT);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT, 1, true),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(ACCENT);
            }
        });
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
    // --- end enhanced theme ---

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().showLogin());
    }

    private void showLogin(){
        frame = new JFrame("School Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(520, 600);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(BG);

        // Top panel with gradient
        JPanel topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, 0, getHeight(), SECONDARY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topPanel.setPreferredSize(new Dimension(520, 150));
        topPanel.setBorder(new EmptyBorder(30, 0, 20, 0));

        JLabel title = new JLabel("Hogwarts School Management", JLabel.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Login to your account", JLabel.CENTER);
        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(new Color(236, 240, 241));

        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(subtitle, BorderLayout.SOUTH);
        frame.add(topPanel, BorderLayout.NORTH);

        // Main login card
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(BG);
        cardPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        JPanel loginCard = createCard();
        loginCard.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(LABEL_FONT);
        userLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        loginCard.add(userLabel, gbc);

        JTextField tfUser = new JTextField(20);
        tfUser.setFont(LABEL_FONT);
        tfUser.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 1;
        loginCard.add(tfUser, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(LABEL_FONT);
        passLabel.setForeground(TEXT_PRIMARY);
        gbc.gridy = 2;
        loginCard.add(passLabel, gbc);

        JPasswordField tfPass = new JPasswordField();
        tfPass.setFont(LABEL_FONT);
        tfPass.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        gbc.gridy = 3;
        loginCard.add(tfPass, gbc);

        JButton btnLogin = new JButton("Login");
        styleButton(btnLogin);
        gbc.gridy = 4; gbc.insets = new Insets(20, 10, 10, 10);
        loginCard.add(btnLogin, gbc);

        cardPanel.add(loginCard);
        frame.add(cardPanel, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> {
            String u = tfUser.getText().trim();
            String pw = new String(tfPass.getPassword()).trim();
            try {
                User user = userDAO.findByUsernameAndPassword(u, pw);
                if (user == null) {
                    showStyledError(frame, "Invalid username or password.", "Login Failed");
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

    private void showStyledError(JFrame parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private void showAdminPanel(){
        JFrame f = new JFrame("Admin Dashboard");
        f.setSize(1200, 700);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        // Enhanced header with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, SECONDARY);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(1200, 80));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel headerTitle = new JLabel("Admin Dashboard");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(6, 15, 6, 15));

        headerPanel.add(headerTitle, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane with custom UI
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(LABEL_FONT);
        tabs.setBackground(CARD_BG);

        // --- Home Tab ---
        JPanel homePanel = new JPanel();
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));
        homePanel.setBackground(BG);
        homePanel.setBorder(new EmptyBorder(40, 60, 40, 60));

        JLabel welcomeLabel = new JLabel("Hogwarts School Management System", JLabel.CENTER);
        welcomeLabel.setFont(TITLE_FONT);
        welcomeLabel.setForeground(PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel infoCard = createCard();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        
        JLabel infoLabel = new JLabel("<html><div style='text-align:center; line-height:1.8;'>"
            + "<p style='font-size:14px; color:#7f8c8d;'>This school provides an atmosphere for students with magical skills</p>"
            + "<p style='font-size:14px; color:#7f8c8d;'>Harry Potter, Ron Weasley and Hermione are proud alumni</p>"
            + "<p style='font-size:14px; color:#7f8c8d;'>Albus Dumbledore, the headmaster, is very encouraging and supportive</p><br>"
            + "<p style='font-size:14px; color:#2c3e50;'><b>Contact:</b> hogwarts@school.edu</p>"
            + "<p style='font-size:14px; color:#2c3e50;'><b>Phone:</b> 1234567890</p>"
            + "</div></html>", JLabel.CENTER);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoCard.add(infoLabel);

        homePanel.add(welcomeLabel);
        homePanel.add(Box.createRigidArea(new Dimension(0, 30)));
        homePanel.add(infoCard);

        // --- Other Tabs ---
        StudentUI studentUI = new StudentUI(studentDAO, userDAO);
        JPanel studentPanel = studentUI.getPanel();
        studentPanel.setBackground(BG);

        TeacherUI teacherUI = new TeacherUI(teacherDAO, userDAO);
        JPanel teacherPanel = teacherUI.getPanel();
        teacherPanel.setBackground(BG);

        ClassUI classUI = new ClassUI(studentDAO, teacherDAO);
        JPanel classPanel = classUI.getPanel();
        classPanel.setBackground(BG);

        tabs.addTab("  Home  ", homePanel);
        tabs.addTab("  Students  ", studentPanel);
        tabs.addTab("  Teachers  ", teacherPanel);
        tabs.addTab("  Classes  ", classPanel);

        mainPanel.add(tabs, BorderLayout.CENTER);
        f.setContentPane(mainPanel);

        logoutBtn.addActionListener(e -> {
            f.dispose();
            new Main().showLogin();
        });

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // ----------------- Teacher Panel -----------------
    private void showTeacherPanel(User user){
        JFrame f = new JFrame("Teacher Dashboard");
        f.setSize(1000, 650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        Teacher teacher = null;
        try {
            teacher = teacherDAO.getById(user.getRefId());
        } catch(SQLException ex){ showErr(ex); }
        
        final Teacher t = teacher; // final reference for lambda

        // Enhanced header
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, ACCENT);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(1000, 80));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        // Left side: Title and class info
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        
        JLabel headerTitle = new JLabel("Teacher Dashboard");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);
        headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel classInfo = new JLabel();
        if (t != null && t.getClassAssigned() != null) {
            classInfo.setText("📚 Class " + t.getClassAssigned());
        } else {
            classInfo.setText("📚 No Class Assigned");
        }
        classInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        classInfo.setForeground(new Color(255, 255, 255));
        classInfo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        titlePanel.add(headerTitle);
        titlePanel.add(classInfo);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(6, 15, 6, 15));

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(LABEL_FONT);
        tabs.setBackground(CARD_BG);

        if (t != null) {
             tabs.addTab("  My Class Students  ", teacherStudentsPanel(t, f));
        }

        mainPanel.add(tabs, BorderLayout.CENTER);
        f.setContentPane(mainPanel);

        logoutBtn.addActionListener(e -> {
            f.dispose();
            new Main().showLogin();
        });

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Enhanced teacherStudentsPanel with search and back button
    private JPanel teacherStudentsPanel(Teacher teacher, JFrame parentFrame){
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(BG);
    p.setBorder(new EmptyBorder(20, 20, 20, 20));

    JPanel searchPanel = createCard();
    searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(25);
        searchField.setFont(LABEL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        JButton searchBtn = new JButton("Search");
        styleButton(searchBtn);
        
        searchPanel.add(new JLabel("Search Student:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        p.add(searchPanel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Name","Age","Class"}, 0
        ) {
            public boolean isCellEditable(int r,int c){return false;}
        };
    JTable table = new JTable(model);
    styleTable(table);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
    p.add(scrollPane, BorderLayout.CENTER);

    JPanel ctrl = createCard();
    ctrl.setLayout(new FlowLayout(FlowLayout.CENTER));
    JButton enterMarks = new JButton("Enter/Update Marks");
    styleAccentButton(enterMarks);
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

            // --- New UI for marks entry ---
            String[] exams = {"First Internal", "Second Internal", "Term Exam"};
            JComboBox<String> examBox = new JComboBox<>(exams);

            String[] subjects = {
                "English", "Maths", "Science", "Social", "Hindi",
                "Computer", "Physics", "Chemistry", "Biology", "History"
            };

            java.util.Map<String, JTextField> subjectFields = new java.util.HashMap<>();

            JPanel marksPanel = new JPanel(new BorderLayout(0, 10));
            marksPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            marksPanel.setBackground(Color.WHITE);

            // Student info at top
            JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 4));
            infoPanel.setOpaque(false);
            JLabel nameLabel = new JLabel("Student: " + s.getName() + " (ID: " + s.getId() + ")");
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            infoPanel.add(nameLabel);
            JPanel examPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            examPanel.setOpaque(false);
            examPanel.add(new JLabel("Examination: "));
            examPanel.add(examBox);
            infoPanel.add(examPanel);
            marksPanel.add(infoPanel, BorderLayout.NORTH);

            // Subjects grid
            JPanel subjectsGrid = new JPanel(new GridLayout(subjects.length, 2, 8, 8));
            subjectsGrid.setOpaque(false);
            for (String subject : subjects) {
                JLabel lbl = new JLabel(subject + ":");
                lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                JTextField field = new JTextField(5);
                field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                subjectFields.put(subject, field);
                subjectsGrid.add(lbl);
                subjectsGrid.add(field);
            }
            marksPanel.add(subjectsGrid, BorderLayout.CENTER);

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
                for (JTextField field : subjectFields.values()) field.setText("");
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
            examBox.addActionListener(evt -> populateMarks.run());
            populateMarks.run();

            JScrollPane scroll = new JScrollPane(marksPanel);
            scroll.setPreferredSize(new Dimension(400, 400));

            int res = JOptionPane.showConfirmDialog(
                null, scroll, "Enter/Update Marks for " + s.getName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );

            if (res == JOptionPane.OK_OPTION) {
                String examSel = (String) examBox.getSelectedItem();
                try {
                    StringBuilder marksSummary = new StringBuilder();
                    for (String subject : subjects) {
                        String raw = subjectFields.get(subject).getText().trim();
                        if (raw.isEmpty()) raw = "0";
                        int markVal;
                        try {
                            markVal = Integer.parseInt(raw);
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(null,
                                    "Invalid mark for \"" + subject + "\". Please enter an integer between 0 and 60.",
                                    "Invalid Mark", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (markVal < 0 || markVal > 60) {
                            JOptionPane.showMessageDialog(null,
                                    "Mark for \"" + subject + "\" must be between 0 and 60.",
                                    "Invalid Mark", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        marksSummary.append(subject).append(":").append(markVal).append(", ");
                    }
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
        JFrame f = new JFrame("Student Dashboard");
        f.setSize(900, 700);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG);

        // Enhanced header with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, new Color(155, 89, 182));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(900, 80));
        headerPanel.setBorder(new EmptyBorder(15, 30, 15, 30));

        JLabel headerTitle = new JLabel("Student Dashboard");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorder(new EmptyBorder(6, 15, 6, 15));

        headerPanel.add(headerTitle, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content area with student profile
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BG);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        try {
            Student s = null;
            for (Student st: studentDAO.getAll()) if (st.getId()==user.getRefId()) s=st;
            if (s==null){ JOptionPane.showMessageDialog(null,"Student profile not found"); return; }
            
            // Welcome message
            JLabel welcomeLabel = new JLabel("Welcome, " + s.getName() + "!", JLabel.CENTER);
            welcomeLabel.setFont(TITLE_FONT);
            welcomeLabel.setForeground(PRIMARY);
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(welcomeLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            
            // Profile Information Card
            JPanel profileCard = createCard();
            profileCard.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 12, 8, 12);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Section header
            JLabel profileHeader = new JLabel("📋 Personal Information");
            profileHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
            profileHeader.setForeground(PRIMARY);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            profileCard.add(profileHeader, gbc);
            gbc.gridwidth = 1;

            gbc.gridy++;
            addProfileRow(profileCard, gbc, "🆔 Admission Number:", s.getAdmissionNumber());
            gbc.gridy++;
            addProfileRow(profileCard, gbc, "👤 Name:", s.getName());
            gbc.gridy++;
            addProfileRow(profileCard, gbc, "📅 Age:", String.valueOf(s.getAge()));
            gbc.gridy++;
            addProfileRow(profileCard, gbc, "🏫 Class:", String.valueOf(s.getClassNo()));
            gbc.gridy++;
            addProfileRow(profileCard, gbc, "⚧ Gender:", s.getGender());
            gbc.gridy++;
            addProfileRow(profileCard, gbc, "🏠 Address:", s.getAddress());
            gbc.gridy++;
            addProfileRow(profileCard, gbc, "🎂 Date of Birth:", s.getDob());
            gbc.gridy++;
            addProfileRow(profileCard, gbc, "👨 Father's Name:", s.getFatherName());
            gbc.gridy++;
            addProfileRow(profileCard, gbc, "📞 Father's Number:", s.getFatherNumber());

            contentPanel.add(profileCard);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Academic Performance Card
            JPanel marksCard = createCard();
            marksCard.setLayout(new GridBagLayout());
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 12, 8, 12);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel marksHeader = new JLabel("📊 Academic Performance");
            marksHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
            marksHeader.setForeground(PRIMARY);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            marksCard.add(marksHeader, gbc);
            gbc.gridwidth = 1;

            gbc.gridy++;
            addMarksSection(marksCard, gbc, "📝 First Internal:", s.getFirstInternal());
            gbc.gridy++;
            addMarksSection(marksCard, gbc, "📝 Second Internal:", s.getSecondInternal());
            gbc.gridy++;
            addMarksSection(marksCard, gbc, "📝 Term Exam:", s.getTermExam());

            contentPanel.add(marksCard);

         } catch(SQLException ex){ showErr(ex); }

        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        f.setContentPane(mainPanel);

        logoutBtn.addActionListener(e -> {
            f.dispose();
             showLogin();
         });

        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Helper method to add profile rows with consistent styling
    private void addProfileRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel lblKey = new JLabel(label);
        lblKey.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKey.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0;
        panel.add(lblKey, gbc);

        JLabel lblValue = new JLabel(value != null ? value : "N/A");
        lblValue.setFont(LABEL_FONT);
        lblValue.setForeground(TEXT_SECONDARY);
        gbc.gridx = 1;
        panel.add(lblValue, gbc);
    }

    // Helper method to add marks section with formatted display
    private void addMarksSection(JPanel panel, GridBagConstraints gbc, String examName, String marksData) {
        JLabel examLabel = new JLabel(examName);
        examLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        examLabel.setForeground(TEXT_PRIMARY);
        gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(examLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;

        if (marksData != null && !marksData.trim().isEmpty()) {
            // Parse and display marks in a nice format
            String[] markPairs = marksData.split(",");
            StringBuilder formattedMarks = new StringBuilder("<html><body style='padding-left:20px;'>");
            int total = 0;
            int count = 0;
            
            for (String pair : markPairs) {
                pair = pair.trim();
                if (pair.contains(":")) {
                    String[] parts = pair.split(":");
                    if (parts.length == 2) {
                        String subject = parts[0].trim();
                        String mark = parts[1].trim();
                        formattedMarks.append("<b>").append(subject).append(":</b> ").append(mark).append("/60<br>");
                        try {
                            total += Integer.parseInt(mark);
                            count++;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            
            if (count > 0) {
                double average = (double) total / count;
                formattedMarks.append("<br><b style='color:#2c3e50;'>Total:</b> ").append(total).append("/").append(count * 60);
                formattedMarks.append("<br><b style='color:#2c3e50;'>Average:</b> ").append(String.format("%.2f", average)).append("/60");
                formattedMarks.append("<br><b style='color:#2c3e50;'>Percentage:</b> ").append(String.format("%.2f", (average / 60) * 100)).append("%");
            }
            
            formattedMarks.append("</body></html>");
            
            JLabel marksLabel = new JLabel(formattedMarks.toString());
            marksLabel.setFont(LABEL_FONT);
            gbc.gridx = 0; gbc.gridwidth = 2;
            panel.add(marksLabel, gbc);
        } else {
            JLabel noMarks = new JLabel("  No marks entered yet");
            noMarks.setFont(LABEL_FONT);
            noMarks.setForeground(TEXT_SECONDARY);
            gbc.gridx = 0; gbc.gridwidth = 2;
            panel.add(noMarks, gbc);
        }
        gbc.gridwidth = 1;
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
        // Build a card-like form with header + content
        panel = new JPanel(new BorderLayout(0,10));
        panel.setBackground(CARD_BG);
        JLabel hdr = new JLabel(t==null ? "Add Teacher" : "Update Teacher", JLabel.CENTER);
        hdr.setFont(TITLE_FONT.deriveFont(16f));
        hdr.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        panel.add(hdr, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,8,6,8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.gridy = 0;

        // helper to style fields
        java.util.function.Consumer<JTextField> styleField = f -> {
            f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189,195,199),1),
                BorderFactory.createEmptyBorder(6,8,6,8)
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
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));
        genderPanel.setOpaque(false);
        genderGroup.add(maleBtn); genderGroup.add(femaleBtn);
        genderPanel.add(maleBtn); genderPanel.add(femaleBtn);
        gbc.gridx = 1; content.add(genderPanel, gbc);
        gbc.gridx = 0; gbc.gridy++;
        content.add(new JLabel("Login username:"), gbc);
        gbc.gridx = 1; content.add(usernameField, gbc);
        gbc.gridx = 0; gbc.gridy++;
        content.add(new JLabel("Login password:"), gbc);
        gbc.gridx = 1; content.add(passwordField, gbc);

        panel.add(content, BorderLayout.CENTER);

        // prefill values if editing
        if (t!=null){
            nameField.setText(t.getName());
            subjectField.setText(t.getSubject());
            qualField.setText(t.getQualification());
            phoneField.setText(t.getPhone_number()!=null? t.getPhone_number() : "");
            if (t.getClassAssigned()!=null) classBox.setSelectedItem(t.getClassAssigned());
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
    // Validate required fields. Returns null when OK, otherwise error message.
    public String validateFields() {
        String nm = nameField.getText() == null ? "" : nameField.getText().trim();
        if (nm.isEmpty()) return "Name is required.";
        return null;
    }
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
        panel = new JPanel(new BorderLayout(0,10));
        panel.setBackground(CARD_BG);
        JLabel hdr = new JLabel(s==null ? "Add Student" : "Update Student", JLabel.CENTER);
        hdr.setFont(TITLE_FONT.deriveFont(16f));
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
                BorderFactory.createEmptyBorder(6,8,6,8)
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
    public void setUsername(String username) { 
        if (username != null) usernameField.setText(username); 
    }
    public void setPassword(String password) { 
        if (password != null) passwordField.setText(password); 
    }
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


