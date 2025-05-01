import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class CostCalculationDialog extends JDialog {
    private JTable partsTable;
    private DefaultTableModel partsTableModel;
    private JSpinner laborSpinner;
    private JSpinner serviceChargeSpinner;
    private JSpinner discountSpinner;
    private JLabel subtotalLabel;
    private JLabel totalLabel;
    private int jobId;
    private EDDTechnologiesSystem system;

    public CostCalculationDialog(Frame owner, int jobId, EDDTechnologiesSystem system) {
        super(owner, "Calculate Repair Cost", true);
        this.jobId = jobId;
        this.system = system;
        initialize();
        loadPartsData();
        calculateTotals();
    }

    private void initialize() {
        setSize(600, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(getOwner());

        // Parts table
        partsTableModel = new DefaultTableModel(new Object[]{"Part", "Quantity", "Unit Cost", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        partsTable = new JTable(partsTableModel);
        add(new JScrollPane(partsTable), BorderLayout.CENTER);

        // Cost controls panel
        JPanel costPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        costPanel.add(new JLabel("Labor Cost ($):"));
        laborSpinner = new JSpinner(new SpinnerNumberModel(50.00, 0.00, 1000.00, 5.00));
        costPanel.add(laborSpinner);

        costPanel.add(new JLabel("Service Charge ($):"));
        serviceChargeSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 500.00, 5.00));
        costPanel.add(serviceChargeSpinner);

        costPanel.add(new JLabel("Discount ($):"));
        discountSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 1000.00, 5.00));
        costPanel.add(discountSpinner);

        costPanel.add(new JLabel("Parts Subtotal:"));
        subtotalLabel = new JLabel("$0.00");
        costPanel.add(subtotalLabel);

        costPanel.add(new JLabel("Total Cost:"));
        totalLabel = new JLabel("$0.00");
        costPanel.add(totalLabel);

        // Add change listeners to auto-update totals
        ChangeListener changeListener = e -> calculateTotals();
        laborSpinner.addChangeListener(changeListener);
        serviceChargeSpinner.addChangeListener(changeListener);
        discountSpinner.addChangeListener(changeListener);

        add(costPanel, BorderLayout.SOUTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save & Notify Customer");
        saveButton.addActionListener(e -> saveCostCalculation());
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void loadPartsData() {
        partsTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT name, quantity, unit_cost FROM parts WHERE job_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, jobId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double unitCost = rs.getDouble("unit_cost");
                double total = quantity * unitCost;

                partsTableModel.addRow(new Object[]{
                        name,
                        quantity,
                        formatCurrency(unitCost),
                        formatCurrency(total)
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading parts: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void calculateTotals() {
        double partsSubtotal = 0.0;

        // Calculate parts subtotal
        for (int i = 0; i < partsTableModel.getRowCount(); i++) {
            String totalStr = partsTableModel.getValueAt(i, 3).toString().replace("$", "");
            partsSubtotal += Double.parseDouble(totalStr);
        }

        double labor = ((Number)laborSpinner.getValue()).doubleValue();
        double serviceCharge = ((Number)serviceChargeSpinner.getValue()).doubleValue();
        double discount = ((Number)discountSpinner.getValue()).doubleValue();

        subtotalLabel.setText(formatCurrency(partsSubtotal));

        double total = partsSubtotal + labor + serviceCharge - discount;
        totalLabel.setText(formatCurrency(total));
    }

    private String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }

    private void saveCostCalculation() {
        double total = Double.parseDouble(totalLabel.getText().replace("$", ""));
        double labor = ((Number)laborSpinner.getValue()).doubleValue();
        double serviceCharge = ((Number)serviceChargeSpinner.getValue()).doubleValue();
        double discount = ((Number)discountSpinner.getValue()).doubleValue();

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

            // Update job with all cost components
            String updateSql = "UPDATE jobs SET " +
                    "total_cost = ?, " +
                    "labor_cost = ?, " +
                    "service_charge = ?, " +
                    "discount = ?, " +
                    "status = 'Ready for Collection', " +
                    "cost_calculated_by = ?, " +
                    "cost_calculated_date = CURRENT_TIMESTAMP " +
                    "WHERE job_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setDouble(1, total);
            updateStmt.setDouble(2, labor);
            updateStmt.setDouble(3, serviceCharge);
            updateStmt.setDouble(4, discount);
            updateStmt.setInt(5, adminId);
            updateStmt.setInt(6, jobId);
            updateStmt.executeUpdate();

            // Record in cost history (optional)
            String historySql = "INSERT INTO job_cost_history " +
                    "(job_id, changed_by, old_total, new_total, notes) " +
                    "SELECT ?, ?, j.total_cost, ?, 'Initial cost calculation' " +
                    "FROM jobs j WHERE j.job_id = ?";
            PreparedStatement historyStmt = conn.prepareStatement(historySql);
            historyStmt.setInt(1, jobId);
            historyStmt.setInt(2, adminId);
            historyStmt.setDouble(3, total);
            historyStmt.setInt(4, jobId);
            historyStmt.executeUpdate();

            // Get customer details for notification
            String customerSql = "SELECT u.user_id, u.email FROM users u JOIN customers c ON u.user_id = c.user_id " +
                    "JOIN equipment e ON c.customer_id = e.customer_id " +
                    "JOIN jobs j ON e.equipment_id = j.equipment_id " +
                    "WHERE j.job_id = ?";
            PreparedStatement customerStmt = conn.prepareStatement(customerSql);
            customerStmt.setInt(1, jobId);
            rs = customerStmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String email = rs.getString("email");

                // Create detailed notification message
                String message = "Your repair is ready for collection.\n\n" +
                        "Itemized Costs:\n" +
                        "- Parts: " + subtotalLabel.getText() + "\n" +
                        "- Labor: " + formatCurrency(labor) + "\n" +
                        "- Service Charge: " + formatCurrency(serviceCharge) + "\n" +
                        "- Discount: -" + formatCurrency(discount) + "\n" +
                        "Total: " + totalLabel.getText();

                // Store notification
                String notifSql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
                PreparedStatement notifStmt = conn.prepareStatement(notifSql);
                notifStmt.setInt(1, userId);
                notifStmt.setString(2, message);
                notifStmt.executeUpdate();

                // In real app, send email here
                System.out.println("Notification sent to " + email + ":\n" + message);
            }

            JOptionPane.showMessageDialog(this, "Cost calculation saved and customer notified!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            system.showAdminPanel(); // Refresh admin panel
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving cost calculation: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}