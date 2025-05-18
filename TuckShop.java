import java.util.ArrayList;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;

public class TuckShop {
    private ArrayList<Item> items;
    private ArrayList<Sale> sales;
    private String adminPasswordHash;
    private static final String DATA_FILE = "tuckshop_data.json";
    public ArrayList<Item> getItems() { return items; }
    public ArrayList<String> getMenuItems() {
        ArrayList<String> menu = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            menu.add((i + 1) + ". " + items.get(i).toString());
        }
        return menu;
    }
    
    public TuckShop() {
        items = new ArrayList<>();
        sales = new ArrayList<>();
        // Default admin password: "admin123" (hashed)
        adminPasswordHash = hashPassword("admin123");
        loadData(); // Load inventory from file
        if (items.isEmpty()) initializeDefaultItems();
    }

    private void initializeDefaultItems() {
        items.add(new Item("BREAD001", "Bread", 15.00, 50));
        items.add(new Item("MILK001", "Milk", 12.50, 30));
        items.add(new Item("EGGS001", "Eggs (6 pack)", 20.00, 20));
        items.add(new Item("SUGAR001", "Sugar (1kg)", 18.00, 25));
        items.add(new Item("FLOUR001", "Flour (1kg)", 16.50, 25));
        items.add(new Item("COKE001", "Coke (2L)", 22.00, 40));
        items.add(new Item("POTATO001", "Potatoes (2kg)", 25.00, 15));
        items.add(new Item("RICE001", "Rice (1kg)", 18.00, 30));
        saveData();
    }

    public void showMenu() {
        System.out.println("\n--- Venterburg Tuck Shop Menu ---");
        if (items.isEmpty()) {
            System.out.println("No items available.");
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).toString());
        }
    }

    public void placeOrder() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Sale> cart = new ArrayList<>();
        double total = 0;

        while (true) {
            showMenu();
            System.out.print("\nEnter item number and quantity (e.g., '1 2' for 2 of item 1, 'q' to checkout): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("q")) break;

            try {
                String[] parts = input.split("\\s+");
                if (parts.length != 2) throw new IllegalArgumentException("Enter item number and quantity");
                int itemIndex = Integer.parseInt(parts[0]) - 1;
                int quantity = Integer.parseInt(parts[1]);

                if (itemIndex < 0 || itemIndex >= items.size()) {
                    System.out.println("Invalid item number.");
                    continue;
                }
                if (quantity <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }

                Item item = items.get(itemIndex);
                item.reduceStock(quantity);
                cart.add(new Sale(item, quantity));
                total += item.getPrice() * quantity;
                System.out.println("Added: " + quantity + " x " + item.getName() + " | Current total: R" + total);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Use numbers for item and quantity.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        if (!cart.isEmpty()) {
            System.out.println("\n--- Order Summary ---");
            for (Sale sale : cart) {
                System.out.println(sale.getItem().getName() + " (x" + sale.getQuantity() + ") - R" + (sale.getItem().getPrice() * sale.getQuantity()));
            }
            System.out.println("Total: R" + total);
            sales.addAll(cart);
            saveData();
        } else {
            System.out.println("No items added to cart.");
        }
    }

    public void adminMenu() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();
        if (!hashPassword(password).equals(adminPasswordHash)) {
            System.out.println("Incorrect password.");
            return;
        }

        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Item");
            System.out.println("2. Restock Item");
            System.out.println("3. View Sales");
            System.out.println("4. Exit Admin Menu");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter item ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Enter item name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter price: ");
                        double price = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter stock: ");
                        int stock = Integer.parseInt(scanner.nextLine());
                        items.add(new Item(id, name, price, stock));
                        saveData();
                        System.out.println("Item added: " + name);
                        break;
                    case "2":
                        showMenu();
                        System.out.print("Enter item number: ");
                        int itemIndex = Integer.parseInt(scanner.nextLine()) - 1;
                        if (itemIndex >= 0 && itemIndex < items.size()) {
                            System.out.print("Enter stock to add: ");
                            int quantity = Integer.parseInt(scanner.nextLine());
                            items.get(itemIndex).addStock(quantity);
                            saveData();
                            System.out.println("Stock updated for " + items.get(itemIndex).getName());
                        } else {
                            System.out.println("Invalid item number.");
                        }
                        break;
                    case "3":
                        System.out.println("\n--- Sales History ---");
                        if (sales.isEmpty()) {
                            System.out.println("No sales recorded.");
                        } else {
                            double totalRevenue = 0;
                            for (Sale sale : sales) {
                                double saleTotal = sale.getItem().getPrice() * sale.getQuantity();
                                System.out.println(sale.getItem().getName() + " (x" + sale.getQuantity() + ") - R" + saleTotal);
                                totalRevenue += saleTotal;
                            }
                            System.out.println("Total Revenue: R" + totalRevenue);
                        }
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Use numbers where required.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing error: " + e.getMessage());
        }
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new File(DATA_FILE))) {
            writer.println("items:");
            for (Item item : items) {
                writer.println(item.getId() + "," + item.getName() + "," + item.getPrice() + "," + item.getStock());
            }
            writer.println("sales:");
            for (Sale sale : sales) {
                writer.println(sale.getItem().getId() + "," + sale.getQuantity());
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        try (Scanner scanner = new Scanner(new File(DATA_FILE))) {
            items.clear();
            sales.clear();
            String section = "";
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.equals("items:")) {
                    section = "items";
                    continue;
                } else if (line.equals("sales:")) {
                    section = "sales";
                    continue;
                }
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    if (section.equals("items") && parts.length == 4) {
                        items.add(new Item(parts[0], parts[1], Double.parseDouble(parts[2]), Integer.parseInt(parts[3])));
                    } else if (section.equals("sales") && parts.length == 2) {
                        String itemId = parts[0];
                        int quantity = Integer.parseInt(parts[1]);
                        for (Item item : items) {
                            if (item.getId().equals(itemId)) {
                                sales.add(new Sale(item, quantity));
                                break;
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        TuckShop shop = new TuckShop();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Venterburg Tuck Shop ---");
            System.out.println("1. View Items");
            System.out.println("2. Place Order");
            System.out.println("3. Admin Menu");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    shop.showMenu();
                    break;
                case "2":
                    shop.placeOrder();
                    break;
                case "3":
                    shop.adminMenu();
                    break;
                case "4":
                    System.out.println("Thank you for visiting the Venterburg Tuck Shop!");
                    return;
                default:
                    System.out.println("Invalid option, please choose again.");
            }
        }
    }
}

class Sale {
    private Item item;
    private int quantity;

    public Sale(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() { return item; }
    public int getQuantity() { return quantity; }
}
