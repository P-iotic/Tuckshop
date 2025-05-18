import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;

public class TuckShopApp extends Application {
    private TuckShop shop = new TuckShop();
    private ListView<String> menuList = new ListView<>();
    private ListView<String> cartList = new ListView<>();
    private Label totalLabel = new Label("Total: R0.00");

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        VBox menuPane = new VBox(10);
        VBox cartPane = new VBox(10);

        // Menu
        menuList.getItems().addAll(shop.getMenuItems());
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        Button addButton = new Button("Add to Cart");
        addButton.setOnAction(e -> addToCart(quantityField.getText()));
        menuPane.getChildren().addAll(new Label("Menu"), menuList, quantityField, addButton);

        // Cart
        Button checkoutButton = new Button("Checkout");
        checkoutButton.setOnAction(e -> checkout());
        cartPane.getChildren().addAll(new Label("Cart"), cartList, totalLabel, checkoutButton);

        root.setLeft(menuPane);
        root.setRight(cartPane);
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Venterburg Tuck Shop");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addToCart(String quantity) {
        try {
            int index = menuList.getSelectionModel().getSelectedIndex();
            int qty = Integer.parseInt(quantity);
            if (index >= 0) {
                Item item = shop.getItems().get(index);
                item.reduceStock(qty);
                cartList.getItems().add(qty + " x " + item.getName() + " - R" + (item.getPrice() * qty));
                double total = Double.parseDouble(totalLabel.getText().replace("Total: R", "")) + item.getPrice() * qty;
                totalLabel.setText("Total: R" + total);
                shop.saveData();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void checkout() {
        if (cartList.getItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cart is empty.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, totalLabel.getText());
            alert.showAndWait();
            cartList.getItems().clear();
            totalLabel.setText("Total: R0.00");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
