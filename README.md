Tuckshop Management System
A console-based Java application for managing a tuckshop, built by Sicelo, a final-year BSc IT student at North-West University. The system supports adding items, purchasing items, tracking stock, and generating sales reports, with data persistence using SQLite.
Features

User Authentication: Admin login with username and password.
Item Management: Add and view items (ID, name, price, quantity).
Stock Management: Low-stock alerts for items with quantity < 5.
Transactions: Buy items with stock updates and transaction logging.
Sales Reporting: View total sales per item.

Technologies

Java 17
SQLite (via sqlite-jdbc)
Visual Studio Code

Setup

Clone the Repository:git clone https://github.com/p-iotic/tuckshop.git
cd tuckshop


Install SQLite JDBC:
Download sqlite-jdbc.jar (e.g., version 3.42.0) from Maven Repository.
Place it in tuckshop/lib/.


Configure Project:
Open the project in Visual Studio Code.
Install the "Extension Pack for Java".
Add lib/sqlite-jdbc.jar to the classpath (Ctrl+Shift+P > "Java: Configure Classpath").


Run the Application:
Open TuckShop.java and click "Run" or press F5.
Login with username: admin, password: admin123.


Test Features:
Add items (e.g., ID: 1, Name: Chips, Price: 10.00, Quantity: 50).
Buy items and check stock updates.
View sales reports.



Project Structure
tuckshop/
├── Item.java
├── Database.java
├── TuckShop.java
├── lib/
│   ├── sqlite-jdbc.jar
├── tuckshop.db (generated on first run)
├── README.md

Future Enhancements

JavaFX GUI for a visual interface.
Customer vs. admin roles.
Export reports to CSV.

License
© 2025 Sicelo. All rights reserved.
