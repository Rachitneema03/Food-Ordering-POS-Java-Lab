# Foodie's Restaurant — Food Ordering POS

A Java 17 desktop food ordering application built with Swing and JDBC (SQLite).

## Features
- Multi-page POS navigation: Dashboard, Order Dashboard, Cart & Checkout, Order History, Reports, Menu Catalogue.
- Product cards with bundled local category artwork, search and category filters.
- Add products to cart; increase/decrease quantities; remove items; clear cart.
- Customer details, Dine-in/Takeaway/Delivery, Cash/UPI/Card payment selection.
- GST calculation (5%), order confirmation and receipt generation.
- SQLite persistence for menu, orders and order line items.
- Local TXT bills saved in `receipts/`.
- Sales summary and top-selling product report.

## Requirements
- JDK 17 or newer
- Maven 3.8+ (for dependency resolution and packaging)

## Run in IntelliJ IDEA / Eclipse
1. Open the `FoodOrderingPOS` folder as an existing Maven project.
2. Wait for Maven to download `org.xerial:sqlite-jdbc`.
3. Run `foodordering.Main` from `src/main/java`.
4. The app creates `data/foodordering.db` automatically on first run.

## Run from terminal (Windows PowerShell)
```powershell
cd FoodOrderingPOS
mvn clean package
java -jar target/food-ordering-pos-1.0.0.jar
```
The Maven Shade plugin packages SQLite JDBC into the runnable JAR.

## Data locations
- `data/foodordering.db` — SQLite database, created at runtime.
- `receipts/FD*.txt` — generated bills, created at runtime.
- `src/main/resources/images/` — local product category artwork.

## Database tables
- `menu_items`: product details, category, price, type, image filename and active flag.
- `orders`: order/customer/payment/tax/total/time information.
- `order_items`: purchased products, quantities and unit prices.

## Notes
- The tax rate is currently configured as 5% in `Order.TAX_RATE`. Change it only if your project instructions require another rate.
- The sample menu is seeded only when the menu table is empty.
- This is an academic POS demonstration, not a production payment gateway. Cash/UPI/Card are stored as payment labels; no payment is actually processed.
- The application uses a local SQLite file and does not require MySQL server setup.

## UI theme
`UITheme` applies explicit foreground and background defaults so text remains readable across Windows light/dark appearance settings. Text inputs, dropdowns, tables, buttons and receipt previews use controlled high-contrast colors.
