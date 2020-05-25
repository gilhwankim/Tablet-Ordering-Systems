package pos;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OrderMenu {

   private SimpleStringProperty name;
   private SimpleIntegerProperty cnt;
   private SimpleIntegerProperty price;
   private SimpleIntegerProperty total;
   
   public OrderMenu(String name, int cnt, int price) {
      this.name = new SimpleStringProperty(name);
      this.cnt = new SimpleIntegerProperty(cnt);
      this.price = new SimpleIntegerProperty(price);
      this.total = new SimpleIntegerProperty(cnt * price);
   }
   
   public String getName() {
      return name.get();
   }

   public void setName(String name) {
      this.name.set(name);
   }

   public int getCnt() {
      return cnt.get();
   }

   public void setCnt(int cnt) {
      this.cnt.set(cnt);
   }

   public int getPrice() {
      return price.get();
   }

   public void setPrice(int price) {
      this.price.set(price);
   }

   public int getTotal() {
      this.total.set(this.getPrice() * this.getCnt());
      return total.get();
   }
   

   
   
}