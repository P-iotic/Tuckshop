import java.util.ArrayList;
import java.util.Scanner;

public class TuckShop {
    private ArrayList<Item> items;

    public TuckShop() {
        items = new ArrayList<>();
        // Add basic necessary items
        items.add(new Item("Bread", 15.00));
        items.add(new Item("Milk", 12.50));
        items.add(new Item("Eggs (6 pack)", 20.00));
        items.add(new Item("Sugar (1kg)", 18.00));
        items.add(new Item("Flour (1kg)", 16.50));
        items.add(new Item("Coke (2L)", 22.00));
        items.add(new Item("Potatoes (2kg)", 25.00));
        items.add(new Item("Rice (1kg)", 18.00));
    }

    public void showMenu() {
        System.out.println("\n--- Welcome to Venterburg Tuck Shop ---");
        System.out.println("Here are the available items:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).toString());
        }
    }

    public void placeOrder() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Item> cart = new ArrayList<>();
        String choice;
        double total = 0;

        do {
            System.out.print("\nEnter the item number to add to your cart (or 'q' to checkout): ");
            choice = scanner.next();

            if (!choice.equalsIgnoreCase("q")) {
                int itemIndex = Integer.parseInt(choice) - 1;
                if (itemIndex >= 0 && itemIndex < items.size()) {
                    Item selectedItem = items.get(itemIndex);
                    cart.add(selectedItem);
                    total += selectedItem.getPrice();
                    System.out.println("Added: " + selectedItem.getName() + " | Current total: R" + total);
                } else {
                    System.out.println("Invalid selection, please try again.");
                }
            }
        } while (!choice.equalsIgnoreCase("q"));

        if (!cart.isEmpty()) {
            System.out.println("\n--- Order Summary ---");
            for (Item item : cart) {
                System.out.println(item.getName() + " - R" + item.getPrice());
            }
            System.out.println("Total: R" + total);
        } else {
            System.out.println("No items were added to your cart.");
        }
    }

    public static void main(String[] args) {
        TuckShop shop = new TuckShop();
        Scanner scanner = new Scanner(System.in);
        String option;

        do {
            System.out.println("\n1. View Items");
            System.out.println("2. Place Order");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            option = scanner.next();

            switch (option) {
                case "1":
                    shop.showMenu();
                    break;
                case "2":
                    shop.placeOrder();
                    break;
                case "3":
                    System.out.println("Thank you for visiting the Venterburg Tuck Shop!");
                    break;
                default:
                    System.out.println("Invalid option, please choose again.");
            }
        } while (!option.equals("3"));
    }
}
