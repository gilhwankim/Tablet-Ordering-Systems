package pos.management;

public class PaymentInfo {
   private String date;
    private String allMenu;
    private String totalPrice;
    private String cardNum;
    private String cash;
    private String payMethod;
   
    public PaymentInfo() {
   
   } 
   public PaymentInfo(String date, String allMenu, String totalPrice, String cardNum, String cash, String payMethod) {
      super();
      this.date = date;
      this.allMenu = allMenu;
      this.totalPrice = totalPrice;
      this.cardNum = cardNum;
      this.cash = cash;
      this.payMethod = payMethod;
   }
   public String getDate() {
      return date;
   }
   public void setDate(String date) {
      this.date = date;
   }
   public String getAllMenu() {
      return allMenu;
   }
   public void setAllMenu(String allMenu) {
      this.allMenu = allMenu;
   }
   public String getTotalPrice() {
      return totalPrice;
   }
   public void setTotalPrice(String totalPrice) {
      this.totalPrice = totalPrice;
   }
   public String getCardNum() {
      return cardNum;
   }
   public void setCardNum(String cardNum) {
      this.cardNum = cardNum;
   }
   public String getCash() {
      return cash;
   }
   public void setCash(String cash) {
      this.cash = cash;
   }
   public String getPayMethod() {
      return payMethod;
   }
   public void setPayMethod(String payMethod) {
      this.payMethod = payMethod;
   }
   
   
}