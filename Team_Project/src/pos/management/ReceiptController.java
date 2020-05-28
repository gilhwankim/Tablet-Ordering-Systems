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
    //���� �ŷ����� ��                           //���� �ݾ� ��
   @FXML private Label currentDate; @FXML private Label totalPrice;
   DecimalFormat df = new DecimalFormat("###,###"); //�������� ��ǥ
   PaymentInfoDao payDao = new PaymentInfoDao(); //�ŷ����� DB                        
   @FXML private TableView<PaymentInfo> receiptTable; //�ŷ�����, �����ݾ�, ������� ���̺� 
   @FXML private TableView<OrderMenu> receiptDetailTable; //�޴���, �ܰ�, ����, �ݾ� ���̺�   
   List<PaymentInfo> payList; //�������� ����Ʈ   
   ObservableList<PaymentInfo> obPayList; //�������� ���̺� ����Ʈ
   List<OrderMenu> omList = new ArrayList<OrderMenu>(); //�� ���������� ���θ޴� ����Ʈ
   ObservableList<OrderMenu> obOmList; //���θ޴� ���̺� ����Ʈ
  
   @Override
   public void initialize(URL location, ResourceBundle resources) {      
      currentDateSetting(currentDate); //���� ��¥ ���
      showDb(); //DB���� �ŷ����� ������
      
      //���̺� ���� ����
      TableColumn<PaymentInfo, ?> dateTc = receiptTable.getColumns().get(0);
      dateTc.setCellValueFactory(new PropertyValueFactory<>("date"));      
      TableColumn<PaymentInfo, ?> totalpayTc = receiptTable.getColumns().get(1);
      totalpayTc.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));      
      TableColumn<PaymentInfo, ?> paymethodTc = receiptTable.getColumns().get(2);
      paymethodTc.setCellValueFactory(new PropertyValueFactory<>("payMethod"));     
      obPayList = FXCollections.observableArrayList(payList);
      receiptTable.setItems(obPayList);    
      ///////////////////////////////////////////////////
      //ū ���̺��� �����ϸ� �������̺� ������ ��µǰ� ��
      receiptTable.getSelectionModel().selectedItemProperty().addListener((p, old, news) ->{
         omList.clear(); //���� �ִ� ������ ����
         totalPrice.setText(df.format(showDetailDB(news)) + "��"); //���γ��� �����ִ� ���ÿ� �Ѱ��� ������ ���Ϲ޾� �󺧿� ������       
      });
   }
   //���� ��¥ ��Ÿ���� �޼���
   public void currentDateSetting(Label currentDateLabel) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      currentDateLabel.setText(sdf.format(new Date()));
   } //�ŷ����� �������� �޼���
   public void showDb() {
      payList = payDao.selectAll(); //DB���� ������   
   }      
   //���̺� Ŭ���� ���������� �޾ƿͼ� �������̺� �����ִ� �޼���
   public int showDetailDB(PaymentInfo paymentInfo) {
      //PaymentInfo�� ������ �����ؼ� �ֱ�
      OrderMenu omTmp;
      //�޴����� ����
      StringTokenizer allSt = new StringTokenizer(paymentInfo.getAllMenu(), "@");
      int stSize = allSt.countTokens(); //�޴���      
      
      for(int i=0; i<stSize; i++) {
         String allMenu = allSt.nextToken();
         //�޴����� ����
         StringTokenizer menuSt = new StringTokenizer(allMenu, "$");
         String name = menuSt.nextToken();
         int count = Integer.parseInt(menuSt.nextToken());
         int price = Integer.parseInt(menuSt.nextToken());
         omTmp =  new OrderMenu(name, count, price);
         omList.add(omTmp);
      }      
      //�������̺� ����
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
      //�� ������ ���� ����
      int totalTmp = 0;
      for(OrderMenu om : omList) {
         totalTmp += om.getTotal();
      }
      return totalTmp; //�Ѱ��� �ݾ� ����      
   }
}
