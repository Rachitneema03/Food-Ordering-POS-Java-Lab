package foodordering;
import java.sql.*; import java.util.*;
public final class MenuRepository {
    public List<FoodItem> getAll() throws SQLException {
        List<FoodItem> out=new ArrayList<>();
        try(Connection c=Database.connect(); PreparedStatement p=c.prepareStatement("SELECT * FROM menu_items WHERE active=1 ORDER BY category,name"); ResultSet r=p.executeQuery()){
            while(r.next()){int id=r.getInt("id"); String type=r.getString("food_type");
                FoodItem f=type.equalsIgnoreCase("Veg")
                    ? new VegItem(id,r.getString("name"),r.getString("category"),r.getDouble("price"),r.getString("description"),r.getString("image_file"))
                    : new NonVegItem(id,r.getString("name"),r.getString("category"),r.getDouble("price"),r.getString("description"),r.getString("image_file"));
                out.add(f);
            }
        } return out;
    }
    public void save(FoodItem item) throws SQLException {
        try(Connection c=Database.connect(); PreparedStatement p=c.prepareStatement("INSERT INTO menu_items(id,name,category,price,description,food_type,image_file) VALUES(?,?,?,?,?,?,?) ON CONFLICT(id) DO UPDATE SET name=excluded.name,category=excluded.category,price=excluded.price,description=excluded.description,food_type=excluded.food_type,image_file=excluded.image_file,active=1")){
            p.setInt(1,item.getId());p.setString(2,item.getName());p.setString(3,item.getCategory());p.setDouble(4,item.getPrice());p.setString(5,item.getDescription());p.setString(6,item.getFoodType());p.setString(7,item.getImageFile());p.executeUpdate();
        }
    }
}