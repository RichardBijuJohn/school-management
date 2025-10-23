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

    public ClassUI(StudentDAO studentDAO, TeacherDAO teacherDAO) {
        this.studentDAO = studentDAO;
        this.teacherDAO = teacherDAO;
    }

    public JPanel getPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 245, 220)); // beige

        // Table model
        DefaultTableModel model = new DefaultTableModel(
                new String[]{"Grade", "No. of Students", "Class Teacher"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 220)); // beige
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(245, 245, 220)); // beige
        JTable table = new JTable(model);
        table.setBackground(new Color(245, 245, 220)); // beige
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton refreshBtn = new JButton("Refresh");
        btnPanel.add(refreshBtn);
        bottomPanel.add(btnPanel, BorderLayout.EAST);
        p.add(bottomPanel, BorderLayout.SOUTH);

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
        refreshBtn.addActionListener(e -> refresh.run());

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
                        studentTable.setRowHeight(24);
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
