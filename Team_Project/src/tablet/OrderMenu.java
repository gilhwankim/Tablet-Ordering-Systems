package tablet;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OrderMenu {

   private SimpleStringProperty name;
   private SimpleIntegerProperty cnt;
   private SimpleStringProperty price;
   private SimpleIntegerProperty totalPrice;
   
   public OrderMenu(String name, int cnt, String price) {
      this.name = new SimpleStringProperty(name);
      this.cnt = new SimpleIntegerProperty(cnt);
      this.price = new SimpleStringProperty(price);
      this.totalPrice = new SimpleIntegerProperty(Integer.parseInt(getPrice()) * getCnt());
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

   public String getPrice() {
      return price.get();
   }

   public void setPrice(String price) {
      this.price.set(price);
   }

   public int getTotalPrice() {
	   this.totalPrice.set(Integer.parseInt(this.getPrice()) * this.getCnt());
      return totalPrice.get();
   }

   
   
}
