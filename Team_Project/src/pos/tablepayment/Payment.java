package pos.tablepayment;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Payment {
   
   private Stage tablePaymentStage;
   
   private TextField billingAmount; //현금결제 화면 청구금액
   private TextField amountOfPayment; //카드결제화면 결제금액
   
   //생성자
   public Payment() {}
   
   //현금결제
   public void cashShow() {
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
          billingAmount = (TextField)cashPayment.lookup("#billingAmount");
          billingAmount.setText("원");
            
       } catch (IOException e) { e.printStackTrace(); }
   }
   
   //카드결제
   public void cardShow() {
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
          amountOfPayment.setText( "원");
            
       } catch (IOException e) { e.printStackTrace(); }
   }
   
}