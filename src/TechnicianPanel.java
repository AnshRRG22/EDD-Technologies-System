import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TechnicianPanel extends JPanel {
    private EDDTechnologiesSystem system;
    private JTable jobsTable;
    private DefaultTableModel jobsTableModel;
    private JButton assessJobButton;
    private JButton addPartsButton;
    private JButton logoutButton;

    public TechnicianPanel(EDDTechnologiesSystem system) {
        this.system = system;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Technician Dashboard - " + system.getCurrentUser(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Jobs table
        jobsTableModel = new DefaultTableModel(new Object[]{"Job ID", "Equipment", "Status", "Problem Description", "Created Date"}, 0);
        jobsTable = new JTable(jobsTableModel);
        add(new JScrollPane(jobsTable), BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        assessJobButton = new JButton("Assess Job");
        assessJobButton.addActionListener(e -> assessJob());
        buttonPanel.add(assessJobButton);

        addPartsButton = new JButton("Add Parts");
        addPartsButton.addActionListener(e -> addParts());
        buttonPanel.add(addPartsButton);

        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> system.showLoginPanel());
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void loadJobs() {
        jobsTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            // Get technician ID
            String techSql = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement techStmt = conn.prepareStatement(techSql);
            techStmt.setString(1, system.getCurrentUser());
            ResultSet techRs = techStmt.executeQuery();

            if (techRs.next()) {
                int techId = techRs.getInt("user_id");

                // Get jobs assigned to this technician
                String jobSql = "SELECT j.job_id, e.type || ' (' || e.brand || ' ' || e.model || ')', j.status, " +
                        "e.problem_description, j.created_date " +
                        "FROM jobs j JOIN equipment e ON j.equipment_id = e.equipment_id " +
                        "WHERE j.technician_id = ? AND j.status != 'Completed'";
                PreparedStatement jobStmt = conn.prepareStatement(jobSql);
                jobStmt.setInt(1, techId);
                ResultSet jobRs = jobStmt.executeQuery();

                while (jobRs.next()) {
                    jobsTableModel.addRow(new Object[]{
                            jobRs.getInt("job_id"),
                            jobRs.getString(2),
                            jobRs.getString("status"),
                            jobRs.getString("problem_description"),
                            jobRs.getTimestamp("created_date")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading jobs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assessJob() {
        int selectedRow = jobsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a job first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jobId = (int) jobsTableModel.getValueAt(selectedRow, 0);
        String status = (String) jobsTableModel.getValueAt(selectedRow, 2);

        if (!status.equals("Job Created")) {
            JOptionPane.showMessageDialog(this, "Job must be in 'Job Created' status", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Job Assessment", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        JTextArea assessmentArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(assessmentArea);

        JButton saveButton = new JButton("Save Assessment");
        saveButton.addActionListener(e -> {
            if (assessmentArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter assessment notes", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE jobs SET assessment_notes = ?, status = 'Job Assessed' WHERE job_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, assessmentArea.getText());
                stmt.setInt(2, jobId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Job assessment saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadJobs();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error saving assessment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(saveButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void addParts() {
        int selectedRow = jobsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a job first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jobId = (int) jobsTableModel.getValueAt(selectedRow, 0);
        String status = (String) jobsTableModel.getValueAt(selectedRow, 2);

        if (!status.equals("Job Assessed")) {
            JOptionPane.showMessageDialog(this, "Job must be in 'Job Assessed' status", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Parts", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        // Parts table
        DefaultTableModel partsModel = new DefaultTableModel(new Object[]{"Part Name", "Description", "Quantity", "Unit Cost", "Supplier"}, 0);
        JTable partsTable = new JTable(partsModel);

        // Load existing parts
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT p.name, p.description, p.quantity, p.unit_cost, s.name AS supplier_name " +
                    "FROM parts p LEFT JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                    "WHERE p.job_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                partsModel.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_cost"),
                        rs.getString("supplier_name")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error loading parts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Form to add new part
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextArea descArea = new JTextArea();
        JScrollPane descScroll = new JScrollPane(descArea);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JTextField costField = new JTextField();

        // Supplier combo
        DefaultComboBoxModel<String> supplierModel = new DefaultComboBoxModel<>();
        supplierModel.addElement("None");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT supplier_id, name FROM suppliers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                supplierModel.addElement(rs.getInt("supplier_id") + " - " + rs.getString("name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error loading suppliers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        JComboBox<String> supplierCombo = new JComboBox<>(supplierModel);

        formPanel.add(new JLabel("Part Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(descScroll);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantitySpinner);
        formPanel.add(new JLabel("Unit Cost:"));
        formPanel.add(costField);
        formPanel.add(new JLabel("Supplier:"));
        formPanel.add(supplierCombo);

        JButton addButton = new JButton("Add Part");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String description = descArea.getText();
                int quantity = (Integer) quantitySpinner.getValue();
                double unitCost = Double.parseDouble(costField.getText());

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter part name", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String selectedSupplier = (String) supplierCombo.getSelectedItem();
                Integer supplierId = null;
                if (selectedSupplier != null && !selectedSupplier.equals("None")) {
                    supplierId = Integer.parseInt(selectedSupplier.split(" - ")[0]);
                }

                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "INSERT INTO parts (job_id, supplier_id, name, description, quantity, unit_cost) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, jobId);
                    if (supplierId != null) {
                        stmt.setInt(2, supplierId);
                    } else {
                        stmt.setNull(2, java.sql.Types.INTEGER);
                    }
                    stmt.setString(3, name);
                    stmt.setString(4, description);
                    stmt.setInt(5, quantity);
                    stmt.setDouble(6, unitCost);
                    stmt.executeUpdate();

                    // Refresh parts table
                    partsModel.addRow(new Object[]{name, description, quantity, unitCost,
                            supplierId != null ? selectedSupplier.split(" - ")[1] : "None"});

                    // Clear form
                    nameField.setText("");
                    descArea.setText("");
                    quantitySpinner.setValue(1);
                    costField.setText("");
                    supplierCombo.setSelectedIndex(0);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid cost", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding part: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(doneButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(partsTable), BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}