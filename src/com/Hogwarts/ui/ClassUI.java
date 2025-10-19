package com.Hogwarts.ui;

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
                            studentModel.addRow(new Object[]{ s.getId(), s.getAdmissionNumber(), s.getName(), s.getAge(), s.getAddress() });
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
                    teacherDAO.assignTeacherToClass(teacherName, classNo);
                    refresh.run();
                    JOptionPane.showMessageDialog(p, "Class added/updated successfully.");
                } catch (Exception ex) { showErr(ex); }
            }
        });

        return p;
    }

    private void showErr(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()); }
}
