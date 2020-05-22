package pos.management;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ReceiptController implements Initializable{
   @FXML private Label currentDate; //���� �ŷ����� ��

   @Override
   public void initialize(URL location, ResourceBundle resources) {      
      currentDateSetting(currentDate); 
    
   }
   //���� ��¥ ��Ÿ���� �޼���
   public void currentDateSetting(Label currentDateLabel) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      currentDateLabel.setText(sdf.format(new Date()));
   }
      
}