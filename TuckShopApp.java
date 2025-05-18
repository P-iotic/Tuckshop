import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class TuckShopApp extends Application {
    private TuckShop shop = new TuckShop();
    private TableView<Item> itemTable = new TableView<>();
    private ListView<String> cartList = new ListView<>();
    private Label totalLabel = new Label("Total: R0.00");
    private double cartTotal = 0.0;
    private ArrayList<Sale> cart = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        // Main layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #121212;");

        // Customer View
        VBox customerPane = createCustomerPane();
        root.setCenter(customerPane);

        // Menu Bar for Admin Access
        MenuBar menuBar = new MenuBar();
        Menu adminMenu = new Menu("Admin");
        MenuItem adminLoginItem = new MenuItem("Login");
        adminLoginItem.setOnAction(e -> showAdminLoginDialog(primaryStage));
        adminMenu.getItems().add(adminLoginItem);
        menuBar.getMenus().add(adminMenu);
        menuBar.setStyle("-fx-background-color: #1f2937;");
        root.setTop(menuBar);

        // Scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Venterburg Tuck Shop");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Refresh table on load
        refreshItemTable();
    }

    private VBox createCustomerPane() {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(20));
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setStyle("-fx-background-color: #121212;");

        // Title
        Label title = new Label("Venterburg Tuck Shop");
        title.setFont(new Font("Source Code Pro", 24));
        title.setTextFill(Color.web("#10b981"));

        // Item Table
        TableColumn<Item, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        TableColumn<Item, Double> priceColumn = new TableColumn<>("Price (R)");
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        TableColumn<Item, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getStock()).asObject());
        itemTable.getColumns().addAll(nameColumn, priceColumn, stockColumn);
        itemTable.setStyle("-fx-background-color: #1f2937; -fx-text-fill: #d1fae5;");
        itemTable.setPrefHeight(300);

        // Add to Cart
        HBox addBox = new HBox(10);
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        quantityField.setStyle("-fx-background-color: #1f2937; -fx-text-fill: #d1fae5;");
        Button addButton = new Button("Add to Cart");
        addButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: #121212;");
        addButton.setOnAction(e -> addToCart(quantityField.getText()));
        addBox.getChildren().addAll(new Label("Quantity:").setTextFill(Color.web("#d1fae5")), quantityField, addButton);

        // Cart
        cartList.setStyle("-fx-background-color: #1f2937; -fx-text-fill: #d1fae5;");
        cartList.setPrefHeight(150);
        totalLabel.setTextFill(Color.web("#d1fae5"));
        Button checkoutButton = new Button("Checkout");
        checkoutButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: #121212;");
        checkoutButton.setOnAction(e -> checkout());

        pane.getChildren().addAll(title, new Label("Available Items").setTextFill(Color.web("#10b981")), itemTable, addBox, new Label("Cart").setTextFill(Color.web("#10b981")), cartList, totalLabel, checkoutButton);
        return pane;
    }

    private void addToCart(String quantityText) {
        try {
            Item selectedItem = itemTable.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                showAlert(Alert.AlertType.ERROR, "No item selected.");
                return;
            }
            int quantity = Integer.parseInt(quantityText);
            int index = shop.getItems().indexOf(selectedItem);
            shop.placeOrder(index, quantity);
            cart.add(new Sale(selectedItem, quantity));
            cartList.getItems().add(quantity + " x " + selectedItem.getName() + " - R" + (selectedItem.getPrice() * quantity));
            cartTotal += selectedItem.getPrice() * quantity;
            totalLabel.setText(String.format("Total: R%.2f", cartTotal));
            refreshItemTable();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid quantity. Enter a number.");
        } catch (IllegalArgumentException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    private void checkout() {
        if (cart.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Cart is empty.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Order placed!\n" + totalLabel.getText());
            cart.clear();
            cartList.getItems().clear();
            cartTotal = 0.0;
            totalLabel.setText("Total: R0.00");
        }
    }

    private void showAdminLoginDialog(Stage owner) {
        Dialog<String> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Admin Login");
        dialog.setHeaderText("Enter Admin Password");

        PasswordField passwordField = new PasswordField();
        VBox content = new VBox(10, new Label("Password:"), passwordField);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) return passwordField.getText();
            return null;
        });

        dialog.showAndWait().ifPresent(password -> {
            if (shop.validateAdminPassword(password)) {
                showAdminPane(owner);
            } else {
                showAlert(Alert.AlertType.ERROR, "Incorrect password.");
            }
        });
    }

    private void showAdminPane(Stage owner) {
        Stage adminStage = new Stage();
        adminStage.initOwner(owner);
        adminStage.setTitle("Admin Panel");

        TabPane tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: #1f2937;");

        // Add Item Tab
        VBox addItemPane = new VBox(10);
        addItemPane.setPadding(new Insets(20));
        TextField idField = new TextField();
        idField.setPromptText("Item ID");
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");
        TextField stockField = new TextField();
        stockField.setPromptText("Stock");
        Button addButton = new Button("Add Item");
        addButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: #121212;");
        addButton.setOnAction(e -> {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int stock = Integer.parseInt(stockField.getText());
                shop.addItem(id, name, price, stock);
                refreshItemTable();
                showAlert(Alert.AlertType.INFORMATION, "Item added: " + name);
                idField.clear();
                nameField.clear();
                priceField.clear();
                stockField.clear();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid price or stock.");
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
        addItemPane.getChildren().addAll(
            new Label("Add New Item").setTextFill(Color.web("#10b981")),
            new Label("ID:").setTextFill(Color.web("#d1fae5")), idField,
            new Label("Name:").setTextFill(Color.web("#d1fae5")), nameField,
            new Label("Price:").setTextFill(Color.web("#d1fae5")), priceField,
            new Label("Stock:").setTextFill(Color.web("#d1fae5")), stockField,
            addButton
        );

        // Restock Tab
        VBox restockPane = new VBox(10);
        restockPane.setPadding(new Insets(20));
        TableView<Item> restockTable = new TableView<>();
        restockTable.setItems(FXCollections.observableArrayList(shop.getItems()));
        restockTable.getColumns().addAll(itemTable.getColumns());
        restockTable.setStyle("-fx-background-color: #1f2937; -fx-text-fill: #d1fae5;");
        TextField restockQuantityField = new TextField();
        restockQuantityField.setPromptText("Quantity to Add");
        Button restockButton = new Button("Restock");
        restockButton.setStyle("-fx-background-color: #10b981; -fx-text-fill: #121212;");
        restockButton.setOnAction(e -> {
            try {
                Item selectedItem = restockTable.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    showAlert(Alert.AlertType.ERROR, "No item selected.");
                    return;
                }
                int quantity = Integer.parseInt(restockQuantityField.getText());
                int index = shop.getItems().indexOf(selectedItem);
                shop.restockItem(index, quantity);
                refreshItemTable();
                restockTable.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Restocked " + selectedItem.getName());
                restockQuantityField.clear();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid quantity.");
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
        restockPane.getChildren().addAll(
            new Label("Restock Items").setTextFill(Color.web("#10b981")),
            restockTable,
            new Label("Quantity:").setTextFill(Color.web("#d1fae5")), restockQuantityField,
            restockButton
        );

        // Sales Tab
        VBox salesPane = new VBox(10);
        salesPane.setPadding(new Insets(20));
        ListView<String> salesList = new ListView<>();
        salesList.setStyle("-fx-background-color: #1f2937; -fx-text-fill: #d1fae5;");
        double totalRevenue = 0;
        for (Sale sale : shop.getSales()) {
            double saleTotal = sale.getItem().getPrice() * sale.getQuantity();
            salesList.getItems().add(sale.getItem().getName() + " (x" + sale.getQuantity() + ") - R" + saleTotal);
            totalRevenue += saleTotal;
        }
        Label revenueLabel = new Label("Total Revenue: R" + String.format("%.2f", totalRevenue));
        revenueLabel.setTextFill(Color.web("#d1fae5"));
        salesPane.getChildren().addAll(
            new Label("Sales History").setTextFill(Color.web("#10b981")),
            salesList,
            revenueLabel
        );

        tabPane.getTabs().addAll(
            new Tab("Add Item", addItemPane),
            new Tab("Restock", restockPane),
            new Tab("Sales", salesPane)
        );

        Scene adminScene = new Scene(tabPane, 600, 400);
        adminStage.setScene(adminScene);
        adminStage.show();
    }

    private void refreshItemTable() {
        itemTable.setItems(FXCollections.observableArrayList(shop.getItems()));
        itemTable.refresh();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
