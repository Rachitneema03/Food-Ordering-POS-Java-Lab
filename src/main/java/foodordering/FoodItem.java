package foodordering;
public abstract class FoodItem {
 private final int id; private final String name,category,description,imageFile; private final double price;
 protected FoodItem(int id,String name,String category,double price,String description,String imageFile){if(id<=0||name==null||name.isBlank()||category==null||category.isBlank()||price<=0)throw new IllegalArgumentException("Invalid menu item details.");this.id=id;this.name=name;this.category=category;this.price=price;this.description=description==null?"":description;this.imageFile=imageFile==null?"":imageFile;}
 public int getId(){return id;} public String getName(){return name;} public String getCategory(){return category;} public double getPrice(){return price;} public String getDescription(){return description;} public String getImageFile(){return imageFile;} public abstract String getFoodType();
 public String toString(){return name;} public boolean equals(Object o){return o instanceof FoodItem f&&id==f.id;} public int hashCode(){return Integer.hashCode(id);}
}
