import java.util.List;
import java.util.Scanner;

public class TuckShop {
    private Database db;
    private Scanner scanner;

    public TuckShop() {
        db = new Database();
        scanner = new Scanner(System.in);
    }

    public boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        return db.authenticateUser(username, password);
    }

    public void addItem() {
        try {
            System.out.print("Enter item ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            if (id <= 0) {
                System.out.println("Error: Item ID must be positive.");
                return;
            }
            if (db.itemExists(id)) {
                System.out.println("Error: Item ID already exists.");
                return;
            }
            System.out.print("Enter item name: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Error: Item name cannot be empty.");
                return;
            }
            System.out.print("Enter item price: ");
            double price = Double.parseDouble(scanner.nextLine());
            if (price <= 0) {
                System.out.println("Error: Price must be positive.");
                return;
            }
            System.out.print("Enter item quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            if (quantity < 0) {
                System.out.println("Error: Quantity cannot be negative.");
                return;
            }
            db.addItem(new Item(id, name, price, quantity));
            System.out.println("Item added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        }
    }

    public void displayItems() {
        List<Item> items = db.getAllItems();
        if (items.isEmpty()) {
            System.out.println("\nNo items available.");
        } else {
            System.out.println("\nAvailable Items:");
            for (Item item : items) {
                System.out.println(item);
                if (item.getQuantity() < 5) {
                    System.out.println("⚠️ Low stock alert for " + item.getName());
                }
            }
        }
    }

    public void buyItem() {
        displayItems();
        if (db.getAllItems().isEmpty()) return;
        try {
            System.out.print("Enter item ID to buy: ");
            int itemId = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());
            if (quantity <= 0) {
                System.out.println("Error: Quantity must be positive.");
                return;
            }
            List<Item> items = db.getAllItems();
            for (Item item : items) {
                if (item.getId() == itemId) {
                    if (item.getQuantity() >= quantity) {
                        double total = quantity * item.getPrice();
                        db.updateItemQuantity(itemId, item.getQuantity() - quantity);
                        db.addTransaction(itemId, quantity, total);
                        System.out.printf("Purchased %d %s for R%.2f\n", quantity, item.getName(), total);
                    } else {
                        System.out.println("Insufficient stock!");
                    }
                    return;
                }
            }
            System.out.println("Item not found!");
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid input format.");
        }
    }

    public void viewSalesReport() {
        List<String> report = db.getSalesReport();
        if (report.isEmpty()) {
            System.out.println("\nNo sales recorded.");
        } else {
            System.out.println("\nSales Report:");
            for (String line : report) {
                System.out.println(line);
            }
        }
    }

    public void exportSalesReport() {
        boolean success = db.exportSalesReportToCSV("sales_report.csv");
        if (success) {
            System.out.println("Sales report exported to sales_report.csv");
        } else {
            System.out.println("Error exporting sales report.");
        }
    }

    public void run() {
        System.out.println("Welcome to TuckShop Management System");
        if (!login()) {
            System.out.println("Login failed. Exiting...");
            return;
        }
        while (true) {
            System.out.println("\n1. Add Item\n2. View Items\n3. Buy Item\n4. View Sales Report\n5. Export Sales Report\n6. Exit");
            System.out.print("Choose an option: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: addItem(); break;
                    case 2: displayItems(); break;
                    case 3: buyItem(); break;
                    case 4: viewSalesReport(); break;
                    case 5: exportSalesReport(); break;
                    case 6: System.out.println("Exiting..."); return;
                    default: System.out.println("Invalid option!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }

    public static void main(String[] args) {
        TuckShop tuckShop = new TuckShop();
        tuckShop.run();
    }
}