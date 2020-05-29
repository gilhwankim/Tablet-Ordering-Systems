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
   
   private TextField billingAmount; //���ݰ��� ȭ�� û���ݾ�
   private TextField amountOfPayment; //ī�����ȭ�� �����ݾ�
   
   //������
   public Payment() {}
   
   //���ݰ���
   public void cashShow() {
      System.out.println("���ݰ���");
       Stage dialog = new Stage(StageStyle.UNDECORATED);
       dialog.initModality(Modality.WINDOW_MODAL); //dialog�� ���(������ ������ ���Ұ�)�� ����
       dialog.initOwner(tablePaymentStage);
        
       try {
          Parent cashPayment = FXMLLoader.load(getClass().getResource("CashPayment.fxml"));
          Scene scene = new Scene(cashPayment);
          dialog.setScene(scene);
          dialog.setResizable(false); //����ڰ� ũ�⸦ �������� ���ϰ� ��
          dialog.show();
            
          //���ݰ��� ȭ�� �ݱ�
          Button cashExitBtn = (Button)cashPayment.lookup("#exit");
          cashExitBtn.setOnMouseClicked(e-> dialog.close());
          
          //û���ݾ�
          billingAmount = (TextField)cashPayment.lookup("#billingAmount");
          billingAmount.setText("��");
            
       } catch (IOException e) { e.printStackTrace(); }
   }
   
   //ī�����
   public void cardShow() {
      System.out.println("ī�����");
       Stage dialog = new Stage(StageStyle.UNDECORATED);
       dialog.initModality(Modality.WINDOW_MODAL); //dialog�� ���(������ ������ ���Ұ�)�� ����
       dialog.initOwner(tablePaymentStage);
         
       try {
          Parent cardPayment = FXMLLoader.load(getClass().getResource("PayingCreditCard.fxml"));
          Scene scene = new Scene(cardPayment);
          dialog.setScene(scene);
          dialog.setResizable(false); //����ڰ� ũ�⸦ �������� ���ϰ� ��
          dialog.show();
          //ī����� ȭ�� �ݱ�
          Button cardExitBtn = (Button)cardPayment.lookup("#exit");
          cardExitBtn.setOnMouseClicked(e-> dialog.close());
          
          //�����ݾ�                                                                                                
          amountOfPayment = (TextField)cardPayment.lookup("#amountOfPayment");
          amountOfPayment.setText( "��");
            
       } catch (IOException e) { e.printStackTrace(); }
   }
   
}