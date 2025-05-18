public class Item {
    private String id; // Unique identifier
    private String name;
    private double price;
    private int stock;

    public Item(String id, String name, double price, int stock) {
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("ID cannot be empty");
        if (name == null || name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        if (stock < 0) throw new IllegalArgumentException("Stock cannot be negative");
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public void reduceStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (quantity > stock) throw new IllegalArgumentException("Insufficient stock for " + name);
        stock -= quantity;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        stock += quantity;
    }

    @Override
    public String toString() {
        return name + " - R" + price + " (Stock: " + stock + ")";
    }
}
