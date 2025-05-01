import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminPanel extends JPanel {
    private EDDTechnologiesSystem system;
    private JTabbedPane tabbedPane;

    // Customers tab
    private JTable customersTable;
    private DefaultTableModel customersTableModel;
    private JButton addCustomerButton;
    private JButton flagCustomerButton;

    // Equipment tab
    private JTable equipmentTable;
    private DefaultTableModel equipmentTableModel;
    private JButton addEquipmentButton;

    // Jobs tab
    private JTable jobsTable;
    private DefaultTableModel jobsTableModel;
    private JButton createJobButton;
    private JButton assignJobButton;
    private JButton costJobButton;

    // Suppliers tab
    private JTable suppliersTable;
    private DefaultTableModel suppliersTableModel;
    private JButton addSupplierButton;
    private JButton removeSupplierButton;

    // Promotions tab
    private JTable promotionsTable;
    private DefaultTableModel promotionsTableModel;
    private JButton addPromotionButton;
    private JButton sendPromotionButton;

    public AdminPanel(EDDTechnologiesSystem system) {
        this.system = system;
        initialize();
    }

    private void initialize() {
        setLayout(new BorderLayout());

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Customers tab
        JPanel customersPanel = new JPanel(new BorderLayout());
        customersTableModel = new DefaultTableModel(new Object[]{"Customer ID", "Name", "Email", "Phone", "Address", "Registered", "Flagged"}, 0);
        customersTable = new JTable(customersTableModel);
        customersPanel.add(new JScrollPane(customersTable), BorderLayout.CENTER);

        JPanel customersButtonPanel = new JPanel(new FlowLayout());
        addCustomerButton = new JButton("Add Walk-in Customer");
        addCustomerButton.addActionListener(e -> showAddCustomerDialog());
        customersButtonPanel.add(addCustomerButton);

        flagCustomerButton = new JButton("Flag/Unflag Customer");
        flagCustomerButton.addActionListener(e -> toggleCustomerFlag());
        customersButtonPanel.add(flagCustomerButton);

        customersPanel.add(customersButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Customers", customersPanel);

        // Equipment tab
        JPanel equipmentPanel = new JPanel(new BorderLayout());
        equipmentTableModel = new DefaultTableModel(new Object[]{"Equipment ID", "Customer", "Type", "Brand", "Model", "Problem"}, 0);
        equipmentTable = new JTable(equipmentTableModel);
        equipmentPanel.add(new JScrollPane(equipmentTable), BorderLayout.CENTER);

        JPanel equipmentButtonPanel = new JPanel(new FlowLayout());
        addEquipmentButton = new JButton("Add Equipment");
        addEquipmentButton.addActionListener(e -> showAddEquipmentDialog());
        equipmentButtonPanel.add(addEquipmentButton);

        equipmentPanel.add(equipmentButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Equipment", equipmentPanel);

        // Jobs tab
        JPanel jobsPanel = new JPanel(new BorderLayout());
        jobsTableModel = new DefaultTableModel(new Object[]{"Job ID", "Equipment", "Technician", "Status", "Created Date"}, 0);
        jobsTable = new JTable(jobsTableModel);
        jobsPanel.add(new JScrollPane(jobsTable), BorderLayout.CENTER);

        JPanel jobsButtonPanel = new JPanel(new FlowLayout());
        createJobButton = new JButton("Create Job");
        createJobButton.addActionListener(e -> showCreateJobDialog());
        jobsButtonPanel.add(createJobButton);

        assignJobButton = new JButton("Assign Technician");
        assignJobButton.addActionListener(e -> assignTechnician());
        jobsButtonPanel.add(assignJobButton);

        costJobButton = new JButton("Calculate Cost");
        costJobButton.addActionListener(e -> calculateJobCost());
        jobsButtonPanel.add(costJobButton);

        jobsPanel.add(jobsButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Jobs", jobsPanel);

        // Suppliers tab
        JPanel suppliersPanel = new JPanel(new BorderLayout());
        suppliersTableModel = new DefaultTableModel(new Object[]{"Supplier ID", "Name", "Contact", "Phone", "Specialization"}, 0);
        suppliersTable = new JTable(suppliersTableModel);
        suppliersPanel.add(new JScrollPane(suppliersTable), BorderLayout.CENTER);

        JPanel suppliersButtonPanel = new JPanel(new FlowLayout());
        addSupplierButton = new JButton("Add Supplier");
        addSupplierButton.addActionListener(e -> showAddSupplierDialog());
        suppliersButtonPanel.add(addSupplierButton);

        removeSupplierButton = new JButton("Remove Supplier");
        removeSupplierButton.addActionListener(e -> removeSupplier());
        suppliersButtonPanel.add(removeSupplierButton);

        suppliersPanel.add(suppliersButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Suppliers", suppliersPanel);

        // Promotions tab
        JPanel promotionsPanel = new JPanel(new BorderLayout());
        promotionsTableModel = new DefaultTableModel(new Object[]{"Promotion ID", "Title", "Start Date", "End Date"}, 0);
        promotionsTable = new JTable(promotionsTableModel);
        promotionsPanel.add(new JScrollPane(promotionsTable), BorderLayout.CENTER);

        JPanel promotionsButtonPanel = new JPanel(new FlowLayout());
        addPromotionButton = new JButton("Add Promotion");
        addPromotionButton.addActionListener(e -> showAddPromotionDialog());
        promotionsButtonPanel.add(addPromotionButton);

        sendPromotionButton = new JButton("Send Promotion");
        sendPromotionButton.addActionListener(e -> sendPromotion());
        promotionsButtonPanel.add(sendPromotionButton);

        promotionsPanel.add(promotionsButtonPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Promotions", promotionsPanel);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> system.showLoginPanel());

        add(tabbedPane, BorderLayout.CENTER);
        add(logoutButton, BorderLayout.SOUTH);
    }

    public void loadData() {
        loadCustomers();
        loadEquipment();
        loadJobs();
        loadSuppliers();
        loadPromotions();
    }

    private void loadCustomers() {
        customersTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.customer_id, u.full_name, u.email, u.phone, c.address, c.is_registered, c.is_flagged " +
                    "FROM customers c JOIN users u ON c.user_id = u.user_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                customersTableModel.addRow(new Object[]{
                        rs.getInt("customer_id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getBoolean("is_registered") ? "Yes" : "No",
                        rs.getBoolean("is_flagged") ? "Yes" : "No"
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading customers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEquipment() {
        equipmentTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT e.equipment_id, u.full_name, e.type, e.brand, e.model, e.problem_description " +
                    "FROM equipment e JOIN customers c ON e.customer_id = c.customer_id " +
                    "JOIN users u ON c.user_id = u.user_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                equipmentTableModel.addRow(new Object[]{
                        rs.getInt("equipment_id"),
                        rs.getString("full_name"),
                        rs.getString("type"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("problem_description")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading equipment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadJobs() {
        jobsTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT j.job_id, e.type || ' (' || e.brand || ' ' || e.model || ')', u.full_name, j.status, j.created_date " +
                    "FROM jobs j JOIN equipment e ON j.equipment_id = e.equipment_id " +
                    "JOIN users u ON j.technician_id = u.user_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                jobsTableModel.addRow(new Object[]{
                        rs.getInt("job_id"),
                        rs.getString(2),
                        rs.getString("full_name"),
                        rs.getString("status"),
                        rs.getTimestamp("created_date")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading jobs: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSuppliers() {
        suppliersTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT supplier_id, name, contact_person, phone, specialization FROM suppliers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                suppliersTableModel.addRow(new Object[]{
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_person"),
                        rs.getString("phone"),
                        rs.getString("specialization")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPromotions() {
        promotionsTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT promotion_id, title, start_date, end_date FROM promotions";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                promotionsTableModel.addRow(new Object[]{
                        rs.getInt("promotion_id"),
                        rs.getString("title"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date")
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading promotions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Walk-in Customer", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));

        JTextField fullNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextArea addressField = new JTextArea();
        JScrollPane addressScroll = new JScrollPane(addressField);

        dialog.add(new JLabel("Full Name:"));
        dialog.add(fullNameField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressScroll);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try (Connection conn = DBConnection.getConnection()) {
                // Start transaction
                conn.setAutoCommit(false);

                // Generate a temporary username
                String username = "walkin_" + System.currentTimeMillis();
                String password = "temp123";

                // Insert into users table
                String userSql = "INSERT INTO users (username, password, full_name, email, phone, user_type) VALUES (?, ?, ?, ?, ?, 'customer')";
                PreparedStatement userStmt = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS);
                userStmt.setString(1, username);
                userStmt.setString(2, password);
                userStmt.setString(3, fullNameField.getText());
                userStmt.setString(4, emailField.getText());
                userStmt.setString(5, phoneField.getText());
                userStmt.executeUpdate();

                // Get generated user ID
                ResultSet rs = userStmt.getGeneratedKeys();
                int userId = 0;
                if (rs.next()) {
                    userId = rs.getInt(1);
                }

                // Insert into customers table
                String customerSql = "INSERT INTO customers (user_id, address, is_registered) VALUES (?, ?, FALSE)";
                PreparedStatement customerStmt = conn.prepareStatement(customerSql);
                customerStmt.setInt(1, userId);
                customerStmt.setString(2, addressField.getText());
                customerStmt.executeUpdate();

                // Commit transaction
                conn.commit();

                JOptionPane.showMessageDialog(dialog, "Walk-in customer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCustomers();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void toggleCustomerFlag() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int customerId = (int) customersTableModel.getValueAt(selectedRow, 0);
        boolean isFlagged = customersTableModel.getValueAt(selectedRow, 6).equals("Yes");

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE customers SET is_flagged = ? WHERE customer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setBoolean(1, !isFlagged);
            stmt.setInt(2, customerId);
            stmt.executeUpdate();

            loadCustomers();
            JOptionPane.showMessageDialog(this, "Customer flag status updated", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating customer flag: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddEquipmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Equipment", true);
        dialog.setSize(400, 400);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));

        // Get customers for dropdown
        DefaultComboBoxModel<String> customerModel = new DefaultComboBoxModel<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.customer_id, u.full_name FROM customers c JOIN users u ON c.user_id = u.user_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                customerModel.addElement(rs.getInt("customer_id") + " - " + rs.getString("full_name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error loading customers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JComboBox<String> customerCombo = new JComboBox<>(customerModel);
        JTextField typeField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JTextField serialField = new JTextField();
        JTextArea problemArea = new JTextArea();
        JScrollPane problemScroll = new JScrollPane(problemArea);

        dialog.add(new JLabel("Customer:"));
        dialog.add(customerCombo);
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

            String selectedCustomer = (String) customerCombo.getSelectedItem();
            int customerId = Integer.parseInt(selectedCustomer.split(" - ")[0]);

            try (Connection conn = DBConnection.getConnection()) {
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

    private void showCreateJobDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Repair Job", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));

        // Get equipment for dropdown
        DefaultComboBoxModel<String> equipmentModel = new DefaultComboBoxModel<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT e.equipment_id, e.type || ' (' || e.brand || ' ' || e.model || ')' " +
                    "FROM equipment e LEFT JOIN jobs j ON e.equipment_id = j.equipment_id " +
                    "WHERE j.job_id IS NULL OR j.status = 'Completed'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                equipmentModel.addElement(rs.getInt("equipment_id") + " - " + rs.getString(2));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Error loading equipment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        JComboBox<String> equipmentCombo = new JComboBox<>(equipmentModel);

        dialog.add(new JLabel("Equipment:"));
        dialog.add(equipmentCombo);

        JButton saveButton = new JButton("Create Job");
        saveButton.addActionListener(e -> {
            if (equipmentCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(dialog, "No equipment available for new job", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedEquipment = (String) equipmentCombo.getSelectedItem();
            int equipmentId = Integer.parseInt(selectedEquipment.split(" - ")[0]);

            try (Connection conn = DBConnection.getConnection()) {
                // Get admin user ID
                String adminSql = "SELECT user_id FROM users WHERE username = ?";
                PreparedStatement adminStmt = conn.prepareStatement(adminSql);
                adminStmt.setString(1, system.getCurrentUser());
                ResultSet rs = adminStmt.executeQuery();
                int adminId = rs.next() ? rs.getInt("user_id") : 0;

                // Create job with default technician (to be assigned later)
                String jobSql = "INSERT INTO jobs (equipment_id, technician_id, admin_id, status) " +
                        "VALUES (?, (SELECT user_id FROM users WHERE user_type = 'technician' LIMIT 1), ?, 'Job Created')";
                PreparedStatement jobStmt = conn.prepareStatement(jobSql);
                jobStmt.setInt(1, equipmentId);
                jobStmt.setInt(2, adminId);
                jobStmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Job created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadJobs();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error creating job: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void assignTechnician() {
        int selectedRow = jobsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a job first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jobId = (int) jobsTableModel.getValueAt(selectedRow, 0);

        // Get technicians for dropdown
        DefaultComboBoxModel<String> techModel = new DefaultComboBoxModel<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT user_id, full_name FROM users WHERE user_type = 'technician'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                techModel.addElement(rs.getInt("user_id") + " - " + rs.getString("full_name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading technicians: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Assign Technician", true);
        dialog.setSize(300, 150);
        dialog.setLayout(new GridLayout(2, 2, 10, 10));

        JComboBox<String> techCombo = new JComboBox<>(techModel);

        dialog.add(new JLabel("Technician:"));
        dialog.add(techCombo);

        JButton assignButton = new JButton("Assign");
        assignButton.addActionListener(e -> {
            String selectedTech = (String) techCombo.getSelectedItem();
            int techId = Integer.parseInt(selectedTech.split(" - ")[0]);

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE jobs SET technician_id = ?, status = 'Job Created' WHERE job_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, techId);
                stmt.setInt(2, jobId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Technician assigned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadJobs();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error assigning technician: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(assignButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void calculateJobCost() {
        int selectedRow = jobsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a job first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jobId = (int) jobsTableModel.getValueAt(selectedRow, 0);
        String status = (String) jobsTableModel.getValueAt(selectedRow, 3);

        if (!status.equals("Job Assessed")) {
            JOptionPane.showMessageDialog(this, "Job must be assessed by technician first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open the new cost calculation dialog
        CostCalculationDialog dialog = new CostCalculationDialog(
                (Frame)SwingUtilities.getWindowAncestor(this),
                jobId,
                this.system
        );
        dialog.setVisible(true);
    }

    private void showAddSupplierDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Supplier", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextArea addressArea = new JTextArea();
        JScrollPane addressScroll = new JScrollPane(addressArea);
        JTextField specializationField = new JTextField();

        dialog.add(new JLabel("Supplier Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Contact Person:"));
        dialog.add(contactField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Phone:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Address:"));
        dialog.add(addressScroll);
        dialog.add(new JLabel("Specialization:"));
        dialog.add(specializationField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (nameField.getText().isEmpty() || phoneField.getText().isEmpty() || addressArea.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO suppliers (name, contact_person, email, phone, address, specialization) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nameField.getText());
                stmt.setString(2, contactField.getText());
                stmt.setString(3, emailField.getText());
                stmt.setString(4, phoneField.getText());
                stmt.setString(5, addressArea.getText());
                stmt.setString(6, specializationField.getText());
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Supplier added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSuppliers();
                dialog.dispose();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding supplier: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void removeSupplier() {
        int selectedRow = suppliersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int supplierId = (int) suppliersTableModel.getValueAt(selectedRow, 0);
        String supplierName = (String) suppliersTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove supplier: " + supplierName + "?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, supplierId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Supplier removed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSuppliers();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error removing supplier: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddPromotionDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Promotion", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));

        JTextField titleField = new JTextField();
        JTextArea descArea = new JTextArea();
        JScrollPane descScroll = new JScrollPane(descArea);
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();

        // Set today's date as default start date
        startDateField.setText(java.time.LocalDate.now().toString());

        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Description:"));
        dialog.add(descScroll);
        dialog.add(new JLabel("Start Date (YYYY-MM-DD):"));
        dialog.add(startDateField);
        dialog.add(new JLabel("End Date (YYYY-MM-DD):"));
        dialog.add(endDateField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            if (titleField.getText().isEmpty() || descArea.getText().isEmpty() ||
                    startDateField.getText().isEmpty() || endDateField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Get admin user ID
                int adminId = 0;
                try (Connection conn = DBConnection.getConnection()) {
                    String adminSql = "SELECT user_id FROM users WHERE username = ?";
                    PreparedStatement adminStmt = conn.prepareStatement(adminSql);
                    adminStmt.setString(1, system.getCurrentUser());
                    ResultSet rs = adminStmt.executeQuery();
                    if (rs.next()) {
                        adminId = rs.getInt("user_id");
                    }

                    String sql = "INSERT INTO promotions (title, description, start_date, end_date, created_by) " +
                            "VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, titleField.getText());
                    stmt.setString(2, descArea.getText());
                    stmt.setDate(3, java.sql.Date.valueOf(startDateField.getText()));
                    stmt.setDate(4, java.sql.Date.valueOf(endDateField.getText()));
                    stmt.setInt(5, adminId);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(dialog, "Promotion added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPromotions();
                    dialog.dispose();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error adding promotion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Please use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    private void sendPromotion() {
        int selectedRow = promotionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a promotion first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int promotionId = (int) promotionsTableModel.getValueAt(selectedRow, 0);
        String promotionTitle = (String) promotionsTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Send promotion '" + promotionTitle + "' to all registered customers?",
                "Confirm Send", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                // Get promotion details
                String promoSql = "SELECT title, description FROM promotions WHERE promotion_id = ?";
                PreparedStatement promoStmt = conn.prepareStatement(promoSql);
                promoStmt.setInt(1, promotionId);
                ResultSet promoRs = promoStmt.executeQuery();

                if (promoRs.next()) {
                    String title = promoRs.getString("title");
                    String description = promoRs.getString("description");
                    String message = "Promotion: " + title + "\n\n" + description;

                    // Send to all registered customers
                    String customerSql = "SELECT u.user_id FROM users u JOIN customers c ON u.user_id = c.user_id WHERE c.is_registered = TRUE";
                    PreparedStatement customerStmt = conn.prepareStatement(customerSql);
                    ResultSet customerRs = customerStmt.executeQuery();

                    int count = 0;
                    while (customerRs.next()) {
                        int userId = customerRs.getInt("user_id");

                        // Insert notification
                        String notifSql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
                        PreparedStatement notifStmt = conn.prepareStatement(notifSql);
                        notifStmt.setInt(1, userId);
                        notifStmt.setString(2, message);
                        notifStmt.executeUpdate();

                        count++;

                        // In a real application, you would also send an email here
                        System.out.println("Promotion sent to user ID: " + userId);
                    }

                    JOptionPane.showMessageDialog(this, "Promotion sent to " + count + " customers", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error sending promotion: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}