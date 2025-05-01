import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerPanel extends JPanel {
    private EDDTechnologiesSystem system;
    private JTable equipmentTable;
    private DefaultTableModel equipmentTableModel;
    private JTable jobsTable;
    private DefaultTableModel jobsTableModel;
    private JTable notificationsTable;
    private DefaultTableModel notificationsTableModel;
    private JButton addEquipmentButton;
    private JButton logoutButton;

    public CustomerPanel(EDDTechnologiesSystem system) {
        this.system = system;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Customer Dashboard - " + system.getCurrentUser(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Equipment tab
        JPanel equipmentPanel = new JPanel(new BorderLayout());
        equipmentTableModel = new DefaultTableModel(new Object[]{"Equipment ID", "Type", "Brand", "Model", "Problem"}, 0);
        equipmentTable = new JTable(equipmentTableModel);
        equipmentPanel.add(new JScrollPane(equipmentTable), BorderLayout.CENTER);

        JPanel equipmentButtonPanel = new JPanel(new FlowLayout());
        addEquipmentButton = new JButton("Add Equipment");
        addEquipmentButton.addActionListener(e -> showAddEquipmentDialog());
        equipmentButtonPanel.add(addEquipmentButton);
        equipmentPanel.add(equipmentButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("My Equipment", equipmentPanel);

        // Jobs tab
        JPanel jobsPanel = new JPanel(new BorderLayout());
        jobsTableModel = new DefaultTableModel(new Object[]{"Job ID", "Equipment", "Status", "Cost", "Created Date"}, 0);
        jobsTable = new JTable(jobsTableModel);
        jobsPanel.add(new JScrollPane(jobsTable), BorderLayout.CENTER);
        tabbedPane.addTab("Repair Jobs", jobsPanel);

        // Notifications tab
        JPanel notificationsPanel = new JPanel(new BorderLayout());
        notificationsTableModel = new DefaultTableModel(new Object[]{"Date", "Message"}, 0);
        notificationsTable = new JTable(notificationsTableModel);
        notificationsPanel.add(new JScrollPane(notificationsTable), BorderLayout.CENTER);
        tabbedPane.addTab("Notifications", notificationsPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Logout button
        logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> system.showLoginPanel());
        add(logoutButton, BorderLayout.SOUTH);
    }

    public void loadData() {
        loadEquipment();
        loadJobs();
        loadNotifications();
    }

    private void loadEquipment() {
        equipmentTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            // Get customer ID
            String customerSql = "SELECT c.customer_id FROM customers c JOIN users u ON c.user_id = u.user_id WHERE u.username = ?";
            PreparedStatement customerStmt = conn.prepareStatement(customerSql);
            customerStmt.setString(1, system.getCurrentUser());
            ResultSet customerRs = customerStmt.executeQuery();

            if (customerRs.next()) {
                int customerId = customerRs.getInt("customer_id");

                // Get customer's equipment
                String equipmentSql = "SELECT equipment_id, type, brand, model, problem_description FROM equipment WHERE customer_id = ?";
                PreparedStatement equipmentStmt = conn.prepareStatement(equipmentSql);
                equipmentStmt.setInt(1, customerId);
                ResultSet equipmentRs = equipmentStmt.executeQuery();

                while (equipmentRs.next()) {
                    equipmentTableModel.addRow(new Object[]{
                            equipmentRs.getInt("equipment_id"),
                            equipmentRs.getString("type"),
                            equipmentRs.getString("brand"),
                            equipmentRs.getString("model"),
                            equipmentRs.getString("problem_description")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading equipment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadJobs() {
        jobsTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            // Get customer ID
            String customerSql = "SELECT c.customer_id FROM customers c JOIN users u ON c.user_id = u.user_id WHERE u.username = ?";
            PreparedStatement customerStmt = conn.prepareStatement(customerSql);
            customerStmt.setString(1, system.getCurrentUser());
            ResultSet customerRs = customerStmt.executeQuery();

            if (customerRs.next()) {
                int customerId = customerRs.getInt("customer_id");

                // Get customer's jobs
                String jobSql = "SELECT j.job_id, e.type || ' (' || e.brand || ' ' || e.model || ')', j.status, " +
                        "j.total_cost, j.created_date " +
                        "FROM jobs j JOIN equipment e ON j.equipment_id = e.equipment_id " +
                        "WHERE e.customer_id = ?";
                PreparedStatement jobStmt = conn.prepareStatement(jobSql);
                jobStmt.setInt(1, customerId);
                ResultSet jobRs = jobStmt.executeQuery();

                while (jobRs.next()) {
                    jobsTableModel.addRow(new Object[]{
                            jobRs.getInt("job_id"),
                            jobRs.getString(2),
                            jobRs.getString("status"),
                            jobRs.getDouble("total_cost") > 0 ? "$" + jobRs.getDouble("total_cost") : "Pending",
                            jobRs.getTimestamp("created_date")
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading jobs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNotifications() {
        notificationsTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            // Get user ID
            String userSql = "SELECT user_id FROM users WHERE username = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setString(1, system.getCurrentUser());
            ResultSet userRs = userStmt.executeQuery();

            if (userRs.next()) {
                int userId = userRs.getInt("user_id");

                // Get notifications
                String notifSql = "SELECT created_date, message FROM notifications WHERE user_id = ? ORDER BY created_date DESC";
                PreparedStatement notifStmt = conn.prepareStatement(notifSql);
                notifStmt.setInt(1, userId);
                ResultSet notifRs = notifStmt.executeQuery();

                while (notifRs.next()) {
                    notificationsTableModel.addRow(new Object[]{
                            notifRs.getTimestamp("created_date"),
                            notifRs.getString("message")
                    });
                }

                // Mark notifications as read
                String updateSql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, userId);
                updateStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading notifications: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddEquipmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Equipment", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField typeField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField serialField = new JTextField();
        JTextArea problemArea = new JTextArea();
        JScrollPane problemScroll = new JScrollPane(problemArea);

        dialog.add(new JLabel("Type:"));
        dialog.add(typeField);
        dialog.add(new JLabel("Brand:"));
        dialog.add(brandField);
        dialog.add(new JLabel("Model:"));
        dialog.add(modelField);
        dialog.add(new JLabel("Serial Number:"));
        dialog.add(serialField);
        dialog.add(new JLabel("Problem Description:"));
        dialog.add(problemScroll);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (typeField.getText().isEmpty() || brandField.getText().isEmpty() || problemArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                // Get customer ID
                String customerSql = "SELECT c.customer_id FROM customers c JOIN users u ON c.user_id = u.user_id WHERE u.username = ?";
                PreparedStatement customerStmt = conn.prepareStatement(customerSql);
                customerStmt.setString(1, system.getCurrentUser());
                ResultSet customerRs = customerStmt.executeQuery();

                if (customerRs.next()) {
                    int customerId = customerRs.getInt("customer_id");

                    String sql = "INSERT INTO equipment (customer_id, type, brand, model, serial_number, problem_description) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, customerId);
                    stmt.setString(2, typeField.getText());
                    stmt.setString(3, brandField.getText());
                    stmt.setString(4, modelField.getText());
                    stmt.setString(5, serialField.getText());
                    stmt.setString(6, problemArea.getText());
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(dialog, "Equipment added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadEquipment();
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding equipment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }
}