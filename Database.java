import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:tuckshop.db";

    public Database() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            // Create items table
            stmt.execute("CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY, name TEXT, price REAL, quantity INTEGER)");
            // Create transactions table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, item_id INTEGER, quantity INTEGER, total REAL, timestamp TEXT)");
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT, role TEXT)");
            // Insert default admin user
            stmt.execute("INSERT OR IGNORE INTO users (username, password, role) VALUES ('admin', 'admin123', 'admin')");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public void addItem(Item item) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO items (id, name, price, quantity) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, item.getId());
            pstmt.setString(2, item.getName());
            pstmt.setDouble(3, item.getPrice());
            pstmt.setInt(4, item.getQuantity());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
    }

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM items");
            while (rs.next()) {
                items.add(new Item(rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("quantity")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching items: " + e.getMessage());
        }
        return items;
    }

    public void updateItemQuantity(int itemId, int quantity) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "UPDATE items SET quantity = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, itemId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating quantity: " + e.getMessage());
        }
    }

    public void addTransaction(int itemId, int quantity, double total) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "INSERT INTO transactions (item_id, quantity, total, timestamp) VALUES (?, ?, ?, datetime('now'))";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, total);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
        }
    }

    public boolean authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error authenticating user: " + e.getMessage());
            return false;
        }
    }

    public List<String> getSalesReport() {
        List<String> report = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT t.item_id, i.name, SUM(t.quantity) as total_qty, SUM(t.total) as total_sales " +
                                             "FROM transactions t JOIN items i ON t.item_id = i.id GROUP BY t.item_id");
            while (rs.next()) {
                report.add(String.format("Item: %s, Sold: %d, Total: R%.2f", 
                    rs.getString("name"), rs.getInt("total_qty"), rs.getDouble("total_sales")));
            }
        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
        return report;
    }
}