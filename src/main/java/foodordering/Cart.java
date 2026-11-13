package foodordering;
import java.util.*;
public final class Cart implements Billable { private final Map<FoodItem,Integer> items=new LinkedHashMap<>();
 public void add(FoodItem f,int q){validate(q);items.put(f,items.getOrDefault(f,0)+q);} public void setQuantity(FoodItem f,int q){validate(q);if(!items.containsKey(f))throw new IllegalArgumentException("Item is not in cart.");items.put(f,q);} public void remove(FoodItem f){items.remove(f);} public void clear(){items.clear();} public boolean isEmpty(){return items.isEmpty();} public Map<FoodItem,Integer> getItems(){return Collections.unmodifiableMap(new LinkedHashMap<>(items));} public int count(){return items.values().stream().mapToInt(Integer::intValue).sum();} public double calculateSubtotal(){return items.entrySet().stream().mapToDouble(e->e.getKey().getPrice()*e.getValue()).sum();} private void validate(int q){if(q<1||q>50)throw new IllegalArgumentException("Quantity must be between 1 and 50.");}
}
