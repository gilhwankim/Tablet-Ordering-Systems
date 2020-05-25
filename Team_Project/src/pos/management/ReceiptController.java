package pos.management;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tablet.OrderMenu;

public class ReceiptController implements Initializable{
   @FXML private Label currentDate; //오늘 거래일자 라벨
   PaymentInfoDao payDao = new PaymentInfoDao();
   @FXML private TableView<PaymentInfo> receiptTable;
   List<PaymentInfo> payList;
   ObservableList<PaymentInfo> obPayList;

   @Override
   public void initialize(URL location, ResourceBundle resources) {      
      currentDateSetting(currentDate); 
      showDb();
      
      TableColumn<PaymentInfo, ?> datetc = receiptTable.getColumns().get(0);
      datetc.setCellValueFactory(new PropertyValueFactory<>("date"));      
      TableColumn<PaymentInfo, ?> totalpaytc = receiptTable.getColumns().get(1);
      totalpaytc.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));      
      TableColumn<PaymentInfo, ?> paymethod = receiptTable.getColumns().get(2);
      paymethod.setCellValueFactory(new PropertyValueFactory<>("payMethod"));     
      
      obPayList = FXCollections.observableArrayList(payList);
      receiptTable.setItems(obPayList);
    
   }
   //오늘 날짜 나타내는 메서드
   public void currentDateSetting(Label currentDateLabel) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      currentDateLabel.setText(sdf.format(new Date()));
   }
   public void showDb() {
      payList = payDao.selectAll();   
   }      
}