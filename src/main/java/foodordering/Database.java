package foodordering;

import java.sql.*;
import java.nio.file.*;

public final class Database {
    private static final String URL = "jdbc:sqlite:data/foodordering.db";

    private Database() {
    }

    public static Connection connect() throws SQLException {
        try {
            Files.createDirectories(Path.of("data"));
        } catch (Exception e) {
            throw new SQLException("Cannot create data directory", e);
        }
        Connection c = DriverManager.getConnection(URL);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON");
            s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS menu_items(
                      id INTEGER PRIMARY KEY, name TEXT NOT NULL, category TEXT NOT NULL,
                      price REAL NOT NULL CHECK(price > 0), description TEXT, food_type TEXT NOT NULL,
                      image_file TEXT NOT NULL, active INTEGER NOT NULL DEFAULT 1)""");
            s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS orders(
                      id INTEGER PRIMARY KEY AUTOINCREMENT, order_code TEXT UNIQUE NOT NULL,
                      customer_name TEXT NOT NULL, phone TEXT, order_type TEXT NOT NULL,
                      payment_mode TEXT NOT NULL, subtotal REAL NOT NULL, tax REAL NOT NULL,
                      total REAL NOT NULL, created_at TEXT NOT NULL)""");
            s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS order_items(
                      id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER NOT NULL,
                      menu_item_id INTEGER NOT NULL, item_name TEXT NOT NULL,
                      quantity INTEGER NOT NULL, unit_price REAL NOT NULL,
                      FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE)""");
            seedMenu(c);
        }
        return c;
    }

    private static void seedMenu(Connection c) throws SQLException {
        try (Statement s=c.createStatement(); ResultSet r=s.executeQuery("SELECT COUNT(*) FROM menu_items")) {
            if (r.next() && r.getInt(1)>0) return;
        }
        Object[][] rows = {
          {101,"Margherita Pizza","Pizza",199,"Tomato, mozzarella and basil","Veg","margherita_pizza.png"},
          {102,"Farmhouse Pizza","Pizza",249,"Capsicum, mushroom, onion and paneer","Veg","farmhouse_pizza.png"},
          {103,"Chicken Pizza","Pizza",299,"Spiced chicken and cheese blend","Non-Veg","chicken_pizza.png"},
          {201,"Veg Burger","Burger",99,"Aloo tikki, lettuce and mint sauce","Veg","veg_burger.png"},
          {202,"Chicken Burger","Burger",149,"Grilled chicken and house sauce","Non-Veg","chicken_burger.png"},
          {203,"Crispy Zinger Burger","Burger",179,"Crispy chicken fillet and lettuce","Non-Veg","crispy_zinger_burger.png"},
          {301,"Paneer Tikka","Starters",179,"Tandoori paneer with spices","Veg","paneer_tikka.png"},
          {302,"Chicken Wings","Starters",249,"Crispy wings with dip","Non-Veg","chicken_wings.png"},
          {303,"Veg Spring Rolls","Starters",129,"Crispy rolls with chilli dip","Veg","veg_spring_rolls.png"},
          {401,"Veg Biryani","Rice",189,"Dum cooked basmati rice","Veg","veg_biryani.png"},
          {402,"Chicken Biryani","Rice",249,"Hyderabadi style with raita","Non-Veg","chicken_biryani.png"},
          {501,"Cold Coffee","Beverages",89,"Chilled coffee with cream","Veg","cold_coffee.png"},
          {502,"Fresh Lime Soda","Beverages",59,"Fresh lime, mint and soda","Veg","fresh_lime_soda.png"},
          {503,"Mango Lassi","Beverages",79,"Mango yoghurt drink","Veg","mango_lassi.png"},
          {601,"Chocolate Brownie","Dessert",119,"Warm chocolate fudge brownie","Veg","chocolate_brownie.png"},
          {602,"Gulab Jamun","Dessert",79,"Soft khoya dumplings in syrup","Veg","gulab_jamun.png"},
          {603,"Ice Cream Sundae","Dessert",99,"Vanilla ice cream and chocolate","Veg","ice_cream_sundae.png"}
        };
        try (PreparedStatement p=c.prepareStatement("INSERT INTO menu_items(id,name,category,price,description,food_type,image_file) VALUES(?,?,?,?,?,?,?)")) {
            for(Object[] a:rows){ for(int i=0;i<a.length;i++) p.setObject(i+1,a[i]); p.addBatch(); } p.executeBatch();
        }
    }
}