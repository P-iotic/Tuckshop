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
        System.out.print("Enter item ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Clear buffer
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter item price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter item quantity: ");
        int quantity = scanner.nextInt();
        db.addItem(new Item(id, name, price, quantity));
        System.out.println("Item added successfully.");
    }

    public void displayItems() {
        List<Item> items = db.getAllItems();
        System.out.println("\nAvailable Items:");
        for (Item item : items) {
            System.out.println(item);
            if (item.getQuantity() < 5) {
                System.out.println("⚠️ Low stock alert for " + item.getName());
            }
        }
    }

    public void buyItem() {
        displayItems();
        System.out.print("Enter item ID to buy: ");
        int itemId = scanner.nextInt();
        System.out.print("Enter quantity: ");
        int quantity = scanner.nextInt();
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
    }

    public void viewSalesReport() {
        List<String> report = db.getSalesReport();
        System.out.println("\nSales Report:");
        for (String line : report) {
            System.out.println(line);
        }
    }

    public void run() {
        System.out.println("Welcome to TuckShop Management System");
        if (!login()) {
            System.out.println("Login failed. Exiting...");
            return;
        }
        while (true) {
            System.out.println("\n1. Add Item\n2. View Items\n3. Buy Item\n4. View Sales Report\n5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer
            switch (choice) {
                case 1: addItem(); break;
                case 2: displayItems(); break;
                case 3: buyItem(); break;
                case 4: viewSalesReport(); break;
                case 5: System.out.println("Exiting..."); return;
                default: System.out.println("Invalid option!");
            }
        }
    }

    public static void main(String[] args) {
        TuckShop tuckShop = new TuckShop();
        tuckShop.run();
    }
}