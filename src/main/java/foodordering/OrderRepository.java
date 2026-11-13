package foodordering;
import java.sql.*; import java.util.*;
public final class OrderRepository {
 public void save(Order o) throws SQLException {
  try(Connection c=Database.connect()){
   c.setAutoCommit(false);
   try {
    try(PreparedStatement p=c.prepareStatement("INSERT INTO orders(order_code,customer_name,phone,order_type,payment_mode,subtotal,tax,total,created_at) VALUES(?,?,?,?,?,?,?,?,?)")){
     p.setString(1,o.getCode());p.setString(2,o.getCustomer());p.setString(3,o.getPhone());p.setString(4,o.getOrderType());p.setString(5,o.getPayment());p.setDouble(6,o.getSubtotal());p.setDouble(7,o.getTax());p.setDouble(8,o.getTotal());p.setString(9,o.getCreatedAt().toString());p.executeUpdate();
    }
    long id; try(Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT last_insert_rowid()")){r.next();id=r.getLong(1);}
    try(PreparedStatement d=c.prepareStatement("INSERT INTO order_items(order_id,menu_item_id,item_name,quantity,unit_price) VALUES(?,?,?,?,?)")){
     for(var e:o.getItems().entrySet()){d.setLong(1,id);d.setInt(2,e.getKey().getId());d.setString(3,e.getKey().getName());d.setInt(4,e.getValue());d.setDouble(5,e.getKey().getPrice());d.addBatch();}d.executeBatch();
    }
    c.commit();
   }catch(SQLException ex){c.rollback();throw ex;}
  }
 }
 public List<String[]> recent() throws SQLException {
  List<String[]> rows=new ArrayList<>();
  try(Connection c=Database.connect();PreparedStatement p=c.prepareStatement("SELECT order_code,customer_name,order_type,total,payment_mode,created_at FROM orders ORDER BY id DESC LIMIT 200");ResultSet r=p.executeQuery()){
   while(r.next())rows.add(new String[]{r.getString(1),r.getString(2),r.getString(3),String.format(Locale.US,"%.2f",r.getDouble(4)),r.getString(5),r.getString(6).replace('T',' ')});
  }return rows;
 }
 public double[] summary() throws SQLException {
  try(Connection c=Database.connect();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*),COALESCE(SUM(total),0),COALESCE(AVG(total),0) FROM orders")){r.next();return new double[]{r.getDouble(1),r.getDouble(2),r.getDouble(3)};}
 }
 public List<String[]> bestSellers() throws SQLException {
  List<String[]> rows=new ArrayList<>();
  try(Connection c=Database.connect();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT item_name,SUM(quantity) q,SUM(quantity*unit_price) revenue FROM order_items GROUP BY item_name ORDER BY q DESC LIMIT 8")){while(r.next())rows.add(new String[]{r.getString(1),String.valueOf(r.getInt(2)),String.format(Locale.US,"%.2f",r.getDouble(3))});}
  return rows;
 }
}
