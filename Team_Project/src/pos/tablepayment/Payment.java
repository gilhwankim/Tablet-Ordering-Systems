package pos.tablepayment;

import java.text.DecimalFormat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pos.OrderMenu;

public class Payment {
   
   private Stage tablePaymentStage;
   public ObservableList<OrderMenu> orderMenu_list = FXCollections.observableArrayList();
   
   private TextField amountOfPayment; //현금,카드결제화면 결제금액
   private DecimalFormat df = new DecimalFormat("###,###"); //단위마다 쉼표

   //생성자
   public Payment() {}
   
   //현금결제
   public void cashShow(int total) {
      System.out.println("현금결제");
       Stage dialog = new Stage(StageStyle.UNDECORATED);
       dialog.initModality(Modality.WINDOW_MODAL); //dialog를 모달(소유자 윈도우 사용불가)로 설정
       dialog.initOwner(tablePaymentStage);
        
       try {
          Parent cashPayment = FXMLLoader.load(getClass().getResource("CashPayment.fxml"));
          Scene scene = new Scene(cashPayment);
          dialog.setScene(scene);
          dialog.setResizable(false); //사용자가 크기를 조절하지 못하게 함
          dialog.show();
            
          //현금결제 화면 닫기
          Button cashExitBtn = (Button)cashPayment.lookup("#exit");
          cashExitBtn.setOnMouseClicked(e-> dialog.close());
          
          //청구금액
          amountOfPayment = (TextField)cashPayment.lookup("#amountOfPayment");
          amountOfPayment.setText(df.format(total) + "원");
          
       } catch (Exception e) { e.printStackTrace(); }
   }
   
   //카드결제
   public void cardShow(int total) {
      System.out.println("카드결제");
       Stage dialog = new Stage(StageStyle.UNDECORATED);
       dialog.initModality(Modality.WINDOW_MODAL); //dialog를 모달(소유자 윈도우 사용불가)로 설정
       dialog.initOwner(tablePaymentStage);
         
       try {
          Parent cardPayment = FXMLLoader.load(getClass().getResource("PayingCreditCard.fxml"));
          Scene scene = new Scene(cardPayment);
          dialog.setScene(scene);
          dialog.setResizable(false); //사용자가 크기를 조절하지 못하게 함
          dialog.show();
          //카드결제 화면 닫기
          Button cardExitBtn = (Button)cardPayment.lookup("#exit");
          cardExitBtn.setOnMouseClicked(e-> dialog.close());
         
          
          //결제금액                                                                                                
          amountOfPayment = (TextField)cardPayment.lookup("#amountOfPayment");
          amountOfPayment.setText(df.format(total) + "원");
       } catch (Exception e) { e.printStackTrace(); }
   }
   
}