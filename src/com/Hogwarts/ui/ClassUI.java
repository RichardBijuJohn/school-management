package com.Hogwarts.ui;

import com.Hogwarts.DAO.ClassDAO;
import com.Hogwarts.DAO.StudentDAO;
import com.Hogwarts.DAO.TeacherDAO;
import com.Hogwarts.model.Student;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ClassUI {
    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;

    // --- Local UI replacements for removed UITheme ---
    private static final Color PRIMARY = new Color(52, 73, 94);
    private static final Color BG = new Color(236, 240, 241);
    private static final Color PANEL = new Color(235, 235, 235);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 16);

    private static void styleButton(AbstractButton b) {
        b.setBackground(new Color(220, 220, 200));
        b.setForeground(Color.DARK_GRAY);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        b.setFont(BUTTON_FONT);
    }

    private static void styleTable(JTable t) {
        t.setFillsViewportHeight(true);
        t.setRowHeight(24);
        t.setSelectionBackground(new Color(200, 200, 255));
        t.getTableHeader().setReorderingAllowed(false);
    }
    // --- end replacements ---

    public ClassUI(StudentDAO studentDAO, TeacherDAO teacherDAO) {
        this.studentDAO = studentDAO;
        this.teacherDAO = teacherDAO;
    }

    public JPanel getPanel() {
    JPanel p = new JPanel(new BorderLayout());
    p.setBackground(BG);

        // Table model
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Grade", "No. of Students", "Class Teacher"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
    table.setBackground(PANEL);
    styleTable(table);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- REFRESH TABLE FUNCTION ---
        Runnable refresh = () -> {
            try {
                model.setRowCount(0);
                // Fix: Ensure classNo is always a string with leading zero if needed (for class 1)
                for (int cls = 1; cls <= 10; cls++) {
                    int count = studentDAO.countByClass(cls);
                    String classTeacherName = teacherDAO.getNameByClass(cls);
                    if (classTeacherName == null || classTeacherName.trim().isEmpty()) {
                        classTeacherName = "Unassigned";
                    }

                
                    ClassDAO classDAO = new ClassDAO();
                    // Fix: Use integer classNo for updateClass and addClass
                    boolean updated = classDAO.updateClass(
                            cls, String.valueOf(cls), count, classTeacherName
                    );
                    if (!updated) {
                        classDAO.addClass(String.valueOf(cls), count, classTeacherName);
                    }

                    model.addRow(new Object[]{cls, count, classTeacherName});
                }
            } catch (SQLException ex) {
                showErr(ex);
            }
        };

        refresh.run();

        // Auto-refresh when panel gains focus (when user switches back to Classes tab)
        p.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refresh.run();
            }
        });

        // --- TABLE CLICK: VIEW STUDENTS IN CLASS ---
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1 && table.getSelectedRow() != -1) {
                    int row = table.getSelectedRow();
                    int classNo = Integer.parseInt(model.getValueAt(row, 0).toString());
                    try {
                        List<Student> students = studentDAO.getByClass(classNo);
                        if (students.isEmpty()) {
                            JOptionPane.showMessageDialog(p,
                                    "No students found in this class.",
                                    "Class Students",
                                    JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        JDialog dialog = new JDialog((Frame) null,
                                "Students in Class " + classNo, true);
                        DefaultTableModel studentModel = new DefaultTableModel(
                                new String[]{"ID", "Admission Number", "Name", "Age", "Gender"}, 0
                        ) {
                            public boolean isCellEditable(int r, int c) {
                                return false;
                            }
                        };
                        for (Student s : students) {
                            studentModel.addRow(new Object[]{
                                    s.getId(),
                                    s.getAdmissionNumber(),
                                    s.getName(),
                                    s.getAge(),
                                    s.getGender()
                            });
                        }

                            JTable studentTable = new JTable(studentModel);
                            styleTable(studentTable);
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
       
        return p;
    }

    private void showErr(Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
    }
}
