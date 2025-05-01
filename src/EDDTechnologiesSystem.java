import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EDDTechnologiesSystem {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private String currentUser;
    private String userType;

    // Panels
    private LoginPanel loginPanel;
    private AdminPanel adminPanel;
    private TechnicianPanel technicianPanel;
    private CustomerPanel customerPanel;
    private RegistrationPanel registrationPanel;

    public EDDTechnologiesSystem() {
        initialize();
    }

    private void initialize() {
        mainFrame = new JFrame("EDD Technologies System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(900, 600);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create panels
        loginPanel = new LoginPanel(this);
        adminPanel = new AdminPanel(this);
        technicianPanel = new TechnicianPanel(this);
        customerPanel = new CustomerPanel(this);
        registrationPanel = new RegistrationPanel(this);

        // Add panels to card layout
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(adminPanel, "Admin");
        cardPanel.add(technicianPanel, "Technician");
        cardPanel.add(customerPanel, "Customer");
        cardPanel.add(registrationPanel, "Registration");

        mainFrame.add(cardPanel);
        showLoginPanel();
        mainFrame.setVisible(true);
    }

    public void showLoginPanel() {
        cardLayout.show(cardPanel, "Login");
    }

    public void showAdminPanel() {
        adminPanel.loadData();
        cardLayout.show(cardPanel, "Admin");
    }

    public void showTechnicianPanel() {
        technicianPanel.loadJobs();
        cardLayout.show(cardPanel, "Technician");
    }

    public void showCustomerPanel() {
        customerPanel.loadData();
        cardLayout.show(cardPanel, "Customer");
    }

    public void showRegistrationPanel() {
        cardLayout.show(cardPanel, "Registration");
    }

    public void login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUser = username;
                userType = rs.getString("user_type");

                switch (userType) {
                    case "admin":
                        showAdminPanel();
                        break;
                    case "technician":
                        showTechnicianPanel();
                        break;
                    case "customer":
                        showCustomerPanel();
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registerUser(String username, String password, String fullName, String email, String phone, String address) {
        try (Connection conn = DBConnection.getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);

            // Insert into users table
            String userSql = "INSERT INTO users (username, password, full_name, email, phone, user_type) VALUES (?, ?, ?, ?, ?, 'customer')";
            PreparedStatement userStmt = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            userStmt.setString(3, fullName);
            userStmt.setString(4, email);
            userStmt.setString(5, phone);
            userStmt.executeUpdate();

            // Get generated user ID
            ResultSet rs = userStmt.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            // Insert into customers table
            String customerSql = "INSERT INTO customers (user_id, address, is_registered) VALUES (?, ?, TRUE)";
            PreparedStatement customerStmt = conn.prepareStatement(customerSql);
            customerStmt.setInt(1, userId);
            customerStmt.setString(2, address);
            customerStmt.executeUpdate();

            // Commit transaction
            conn.commit();

            JOptionPane.showMessageDialog(mainFrame, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            showLoginPanel();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainFrame, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getUserType() {
        return userType;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EDDTechnologiesSystem());
    }
}