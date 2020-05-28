package tablet;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Bill {

	private Stage clientStage = TabletMain.clientStage;
	private StringTokenizer st1;
	private StringTokenizer st2;
	
	
	public Bill() {}
	
	//테이블별 계산서 부르는 메서드
	public void show(ObservableList<OrderMenu> orderTableTotal, String msg ) {          
          Stage dialog = new Stage(StageStyle.UNDECORATED);           
             dialog.initModality(Modality.WINDOW_MODAL); //dialog를 모달(소유자 윈도우 사용불가)로 설정
             dialog.initOwner(clientStage);        
             
             Parent tableBill;
           try {
              tableBill = FXMLLoader.load(getClass().getResource("tableBill.fxml"));
              Button billExitBtn = (Button)tableBill.lookup("#exit");
              Label totalPrice = (Label)tableBill.lookup("#totalPrice");
              
              @SuppressWarnings("unchecked")
              TableView<OrderMenu> billTable = (TableView<OrderMenu>) tableBill.lookup("#billTable");
              
              TableColumn<OrderMenu, ?> att1 = billTable.getColumns().get(0);
              att1.setCellValueFactory(new PropertyValueFactory<>("name"));
              att1.setText("메뉴");                
              TableColumn<OrderMenu, ?> att2 = billTable.getColumns().get(1);
              att2.setCellValueFactory(new PropertyValueFactory<>("cnt"));
              att2.setText("수량");                
              TableColumn<OrderMenu, ?> att3 = billTable.getColumns().get(2);
              att3.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
              att3.setText("가격");
              
              orderTableTotal.clear();
              st2 = new StringTokenizer(msg, "@@");
              while(st2.hasMoreTokens()) {
                 st1 = new StringTokenizer(st2.nextToken(), "$$");
                 orderTableTotal.add(new OrderMenu(st1.nextToken(), Integer.parseInt(st1.nextToken()), st1.nextToken()));
              }
              
              if(orderTableTotal.size()==0){ //하나도 주문 안했으면 아무것도 안적힘
                 billTable.setPlaceholder(new Label(""));
                 totalPrice.setText("");
              }else { //주문을 했다면 계산서 나옴
                 billTable.setItems(orderTableTotal); //테이블뷰에 세팅   
                 
                 int totalResult = 0;
                 DecimalFormat df = new DecimalFormat("###,###"); //단위마다 쉼표
                 for(OrderMenu om : orderTableTotal) {
                   totalResult += om.getTotalPrice(); //시킨 메뉴 가격을 더함
                 }
                 totalPrice.setText((df.format(totalResult)) + "원"); //현재까지 주문한 가격 출력                   
              }
              //tableBill의 X표시 누르면 창닫힘
              billExitBtn.setOnMouseClicked(e -> dialog.close());
              
              Scene scene = new Scene(tableBill);            
                dialog.setScene(scene);
                dialog.setResizable(false);  //사용자가 크기를 조절하지 못하게 함
                dialog.show();       
           } catch (IOException e) {
        	   e.printStackTrace(); 
           }      
      }         
}
