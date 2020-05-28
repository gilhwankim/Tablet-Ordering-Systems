package pos.management;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pos.OrderMenu;

public class ReceiptController implements Initializable{
    //오늘 거래일자 라벨                           //결제 금액 라벨
   @FXML private Label currentDate; @FXML private Label totalPrice;
   DecimalFormat df = new DecimalFormat("###,###"); //단위마다 쉼표
   PaymentInfoDao payDao = new PaymentInfoDao(); //거래내역 DB                        
   @FXML private TableView<PaymentInfo> receiptTable; //거래일자, 결제금액, 결제방법 테이블 
   @FXML private TableView<OrderMenu> receiptDetailTable; //메뉴명, 단가, 수량, 금액 테이블   
   List<PaymentInfo> payList; //결제내역 리스트   
   ObservableList<PaymentInfo> obPayList; //결제내역 테이블 리스트
   List<OrderMenu> omList = new ArrayList<OrderMenu>(); //각 결제내역의 세부메뉴 리스트
   ObservableList<OrderMenu> obOmList; //세부메뉴 테이블 리스트
  
   @Override
   public void initialize(URL location, ResourceBundle resources) {      
      currentDateSetting(currentDate); //현재 날짜 출력
      showDb(); //DB에서 거래내역 가져옴
      
      //테이블에 내용 세팅
      TableColumn<PaymentInfo, ?> dateTc = receiptTable.getColumns().get(0);
      dateTc.setCellValueFactory(new PropertyValueFactory<>("date"));      
      TableColumn<PaymentInfo, ?> totalpayTc = receiptTable.getColumns().get(1);
      totalpayTc.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));      
      TableColumn<PaymentInfo, ?> paymethodTc = receiptTable.getColumns().get(2);
      paymethodTc.setCellValueFactory(new PropertyValueFactory<>("payMethod"));     
      obPayList = FXCollections.observableArrayList(payList);
      receiptTable.setItems(obPayList);    
      ///////////////////////////////////////////////////
      //큰 테이블에서 선택하면 세부테이블에 내용이 출력되게 함
      receiptTable.getSelectionModel().selectedItemProperty().addListener((p, old, news) ->{
         omList.clear(); //전에 있던 내용은 없앰
         totalPrice.setText(df.format(showDetailDB(news)) + "원"); //세부내용 보여주는 동시에 총결제 가격을 리턴받아 라벨에 보여줌       
      });
   }
   //오늘 날짜 나타내는 메서드
   public void currentDateSetting(Label currentDateLabel) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      currentDateLabel.setText(sdf.format(new Date()));
   } //거래내역 가져오는 메서드
   public void showDb() {
      payList = payDao.selectAll(); //DB에서 가져옴   
   }      
   //테이블에 클릭된 결제내역을 받아와서 세부테이블에 보여주는 메서드
   public int showDetailDB(PaymentInfo paymentInfo) {
      //PaymentInfo의 정보를 정제해서 넣기
      OrderMenu omTmp;
      //메뉴별로 나눔
      StringTokenizer allSt = new StringTokenizer(paymentInfo.getAllMenu(), "@");
      int stSize = allSt.countTokens(); //메뉴수      
      
      for(int i=0; i<stSize; i++) {
         String allMenu = allSt.nextToken();
         //메뉴별로 나눔
         StringTokenizer menuSt = new StringTokenizer(allMenu, "$");
         String name = menuSt.nextToken();
         int count = Integer.parseInt(menuSt.nextToken());
         int price = Integer.parseInt(menuSt.nextToken());
         omTmp =  new OrderMenu(name, count, price);
         omList.add(omTmp);
      }      
      //세부테이블에 세팅
      TableColumn<OrderMenu, ?> menuNameTc = receiptDetailTable.getColumns().get(0);
      menuNameTc.setCellValueFactory(new PropertyValueFactory<>("name"));      
      TableColumn<OrderMenu, ?> menuPriceTc = receiptDetailTable.getColumns().get(1);
      menuPriceTc.setCellValueFactory(new PropertyValueFactory<>("price"));      
      TableColumn<OrderMenu, ?> menuCountTc = receiptDetailTable.getColumns().get(2);
      menuCountTc.setCellValueFactory(new PropertyValueFactory<>("cnt"));
      TableColumn<OrderMenu, ?> totalPriceTc = receiptDetailTable.getColumns().get(3);
      totalPriceTc.setCellValueFactory(new PropertyValueFactory<>("total"));
      obOmList = FXCollections.observableArrayList(omList);
      receiptDetailTable.setItems(obOmList);  
      //총 결제액 담을 변수
      int totalTmp = 0;
      for(OrderMenu om : omList) {
         totalTmp += om.getTotal();
      }
      return totalTmp; //총결제 금액 리턴      
   }
}
