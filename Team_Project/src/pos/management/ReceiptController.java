package pos.management;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class ReceiptController implements Initializable{
   @FXML private Label currentDate; //오늘 거래일자 라벨

   @Override
   public void initialize(URL location, ResourceBundle resources) {      
      currentDateSetting(currentDate); 
    
   }
   //오늘 날짜 나타내는 메서드
   public void currentDateSetting(Label currentDateLabel) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      currentDateLabel.setText(sdf.format(new Date()));
   }
      
}