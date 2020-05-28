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
	
	//���̺� ��꼭 �θ��� �޼���
	public void show(ObservableList<OrderMenu> orderTableTotal, String msg ) {          
          Stage dialog = new Stage(StageStyle.UNDECORATED);           
             dialog.initModality(Modality.WINDOW_MODAL); //dialog�� ���(������ ������ ���Ұ�)�� ����
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
              att1.setText("�޴�");                
              TableColumn<OrderMenu, ?> att2 = billTable.getColumns().get(1);
              att2.setCellValueFactory(new PropertyValueFactory<>("cnt"));
              att2.setText("����");                
              TableColumn<OrderMenu, ?> att3 = billTable.getColumns().get(2);
              att3.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
              att3.setText("����");
              
              orderTableTotal.clear();
              st2 = new StringTokenizer(msg, "@@");
              while(st2.hasMoreTokens()) {
                 st1 = new StringTokenizer(st2.nextToken(), "$$");
                 orderTableTotal.add(new OrderMenu(st1.nextToken(), Integer.parseInt(st1.nextToken()), st1.nextToken()));
              }
              
              if(orderTableTotal.size()==0){ //�ϳ��� �ֹ� �������� �ƹ��͵� ������
                 billTable.setPlaceholder(new Label(""));
                 totalPrice.setText("");
              }else { //�ֹ��� �ߴٸ� ��꼭 ����
                 billTable.setItems(orderTableTotal); //���̺�信 ����   
                 
                 int totalResult = 0;
                 DecimalFormat df = new DecimalFormat("###,###"); //�������� ��ǥ
                 for(OrderMenu om : orderTableTotal) {
                   totalResult += om.getTotalPrice(); //��Ų �޴� ������ ����
                 }
                 totalPrice.setText((df.format(totalResult)) + "��"); //������� �ֹ��� ���� ���                   
              }
              //tableBill�� Xǥ�� ������ â����
              billExitBtn.setOnMouseClicked(e -> dialog.close());
              
              Scene scene = new Scene(tableBill);            
                dialog.setScene(scene);
                dialog.setResizable(false);  //����ڰ� ũ�⸦ �������� ���ϰ� ��
                dialog.show();       
           } catch (IOException e) {
        	   e.printStackTrace(); 
           }      
      }         
}
