package tablet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pos.menu.Menu;

public class TabletController implements Initializable{

   private Stage clientStage = TabletMain.clientStage;
   Stage stage;
   
   //ó�� �ڸ����ϴ� â
   private Button btn;
   private ChoiceBox<String> cb;
   private ObservableList<String> ol = FXCollections.observableArrayList();
   
   //�������ῡ �ʿ��� ���
   private Socket socket;
   private InputStream is;
   private OutputStream os;
   private DataInputStream dis;
   private DataOutputStream dos;
   
   private List<Menu> menuList = new ArrayList<>();
   private StringTokenizer st1;
   private StringTokenizer st2;
   
   private @FXML TableView<OrderMenu> orderTable;
   private ObservableList<OrderMenu> orderTableOl = FXCollections.observableArrayList();
 //���̺��� �ֹ��� ��ü ����Ʈ
   private ObservableList<OrderMenu> orderTableTotal = FXCollections.observableArrayList();
   private @FXML Button orderBtn;
   private @FXML Label total; 
   private @FXML Label tableNo; //���ø��� ���̺��ȣ ǥ�� ��
   private @FXML Button billBtn; //��꼭 ȣ�� ��ư
   private @FXML Button subtractBtn; // - ��ư
   private @FXML Button plusBtn; // + ��ư
   private @FXML TabPane tp;
   
   
   
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
      //�����ư
     clientStage.setOnCloseRequest(e -> stopClient());
      
      tableSet();
      orderTable.setItems(orderTableOl);
      orderTable.setPlaceholder(new Label(""));
      
      billBtn.setOnAction((event)-> callBill(event)); //��꼭 ��ư �޼���
      plusBtn.setOnAction( e -> plusBtnAction(e));
      subtractBtn.setOnAction( e -> subtractBtnAction(e));
      orderBtn.setOnAction(e -> orderBtnAction(e));
      
   }
   
   //ó�� �ö���� �ڸ����ϴ� â
   @SuppressWarnings("unchecked")
   private void tableSet() {
      Parent settableNo = null;
      try {
         settableNo = FXMLLoader.load(getClass().getResource("selectTablenum.fxml")); 
      } catch (Exception e) {
      }
      btn = (Button)settableNo.lookup("#btn");
      cb = (ChoiceBox<String>)settableNo.lookup("#cb");
      ol.addAll("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15");
      cb.setItems(ol);
      
      Scene scene = new Scene(settableNo);
      stage = new Stage();
      stage.setScene(scene);
      stage.show();
      
      btn.setOnAction( e -> {
         String tableNo = cb.getSelectionModel().getSelectedItem();
         if(tableNo == null) {
            return;
         }
         if(Integer.parseInt(tableNo)<10) { //���̺� ��ȣ 10���ϸ� 0 + ���̺� ��ȣ
            this.tableNo.setText("0" + tableNo);             
         }else {
            this.tableNo.setText(tableNo); //10�̻��� �״�� ���
         }         
         if(tableNo != null) {
            startClient(tableNo);
         }
      });
   }
   
   private void startClient(String tableNo) {
      try {
         socket = new Socket();
         socket.connect(new InetSocketAddress("localhost", 8888));
         
         is = socket.getInputStream();
         dis = new DataInputStream(is);
         os = socket.getOutputStream();
         dos = new DataOutputStream(os);
         //�����ϰ� ���̺� ��ȣ�� ������ ����
         dos.writeUTF(tableNo);
         
         //���̸��� ���� ���̺��� �ִ��� ������ üũ (�����ϸ� connOk, ���н� connFail)
         String message = dis.readUTF();
         
         //false�� ������ ���Ͻ��� �ٽ� �ڸ����ϰ��Ѵ�.
         if(!connCheck(message)) {
            return;
         }
         stage.close();
         clientStage.show();
         
         //�޴��� �����κ��� �޴´�.
         String menu = dis.readUTF();
         //�޴��� �޾ƿ��µ��� �������� ��
         if(menu != null) {
            st1 = new StringTokenizer(menu, "@@");
            System.out.println("�������� ��ü �޾ƿ� �޴�" + menu);
            while(st1.hasMoreTokens()) {
               String tmp = st1.nextToken();
               st2 = new StringTokenizer(tmp, "$$");
               menuList.add(new Menu(Integer.parseInt(st2.nextToken()),st2.nextToken(), st2.nextToken(), st2.nextToken()));
            }
            
         for(Menu m : menuList) {
            System.out.println("�޴�����Ʈ Ȯ��:"+m.getMenuNum()+","+m.getCategory()+","+m.getName()+","+m.getPrice()+",");
         }
         
         	MakeTab mt = new MakeTab();
         	tp = mt.make(menuList, tp);
         	addMenu();
            orderTableSetting();
            
         }else {
            throw new Exception(); 
         }
      }catch (Exception e) {
         System.out.println("�޴� ������ �޾ƿ��� ����.");
         e.printStackTrace();
      }
   }
   
   private void stopClient() {
         try {
            dos.writeUTF("����///�º�");
            dos.flush();
            if(!socket.isClosed()) {
                 socket.close();
               }
            is.close();
               dis.close();
               os.close();
               dos.close();
               System.out.println("�º� ����.");
               Platform.exit();
               System.exit(0);
              }catch (Exception e) {
                 System.exit(0);
              }
      }
   
   //�޴��ǿ� �޴��� �ִ� �޼���
    @SuppressWarnings("unchecked")
	private void addMenu(){
    	  for(Menu m : menuList) {
	         try {
	            //�� �޴� ������
	            VBox node = FXMLLoader.load(getClass().getResource("menuItem.fxml"));
	            Label labelName = (Label)node.lookup("#labelName");
	            Label labelPrice = (Label)node.lookup("#labelPrice");
	            //menuItem.fxml���� imageView ã�ƿ�
	            ImageView imageMenu = (ImageView)node.lookup("#menuImg");
	            
	            try {
	            	 //�޴��̸��� ���� �̹����� �����
		            imageMenu.setImage(new Image(getClass().getResource(
		                    "/images/" + m.getName() + ".jpg").toString()));   
	            }catch (Exception e) {
	            	 imageMenu.setImage(new Image(getClass().getResource(
			                    "/images/noImage.jpg").toString()));   
				}
	            labelName.setText(m.getName());
	            labelPrice.setText(m.getPrice());            
	            
	            node.setOnMouseClicked(e -> {
	               if(e.getClickCount() == 2) {
	                  System.out.println("�޴��̸� : " + labelName.getText() + "�޴����� : " + labelPrice.getText());
	                  addOrdertable(labelName.getText());
	               }
	            });
	            
	            for(Tab t : tp.getTabs()) {
	            	if(t.getText().equals(m.getCategory())) {
		            	HBox h = (HBox)t.getContent();
		            	VBox v = (VBox)h.getChildren().get(0);
		            	ListView<HBox> lv = (ListView<HBox>)v.getChildren().get(0);
		            	if(lv.getItems().size() == 0) {
		            		HBox hbox = new HBox();
		            		hbox.setSpacing(10);
		            		hbox.getChildren().add(node);
		            		lv.getItems().add(hbox);
		            		break;
		            	}else if(lv.getItems().get(lv.getItems().size() - 1).getChildren().size() % 3 == 0 ) {
		            		HBox hbox = new HBox();
		            		hbox.setSpacing(10);
		            		hbox.getChildren().add(node);
		            		lv.getItems().add(hbox);
		            		break;
		            	}else {
		            		lv.getItems().get(lv.getItems().size()-1).getChildren().add(node);
		            		break;
		            	}
	            	}
	            }
	         }catch (Exception e) {
	        	 e.printStackTrace();
	         }
    	  }
      }
      
      //���̺�� �ʱ�ȭ
      private void orderTableSetting() {
         TableColumn<OrderMenu, ?> a = orderTable.getColumns().get(0);
          a.setCellValueFactory(new PropertyValueFactory<>("name"));
          a.setText("�޴� ��");
          
          TableColumn<OrderMenu, ?> b = orderTable.getColumns().get(1);
          b.setCellValueFactory(new PropertyValueFactory<>("cnt"));
          b.setText("����");
          
          TableColumn<OrderMenu, ?> c = orderTable.getColumns().get(2);
          c.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
          c.setText("����");
          
      }
      
      //���� ���̺� �޴� �ֱ�.
      private void addOrdertable(String name) {
         try {
         Menu mTmp = null;
         for(Menu m : menuList) {
            if(m.getName().equals(name)) {
               mTmp = m;
            }
         }
         for(OrderMenu om : orderTableOl) {
            if(om.getName().equals(mTmp.getName())) {
               om.setCnt(om.getCnt() + 1);
               om.setTotalPrice(Integer.parseInt(om.getPrice()));
               int idx = orderTableOl.indexOf(om);
               OrderMenu om2 = om;
               orderTableOl.remove(om);
               orderTableOl.add(idx, om2);
               orderTable.refresh();
               priceUpdate();
               return;
            }
         }
         OrderMenu om = new OrderMenu(mTmp.getName(), 1, mTmp.getPrice());
         orderTableOl.add(om);
         orderTable.refresh();
         priceUpdate();
         }catch (Exception e) {
            e.printStackTrace();
         }
         
      }
   
      //'�ֹ��ϱ�'��ư�� �׼�
      private void orderBtnAction(ActionEvent event) {
         try {
         String msg = "";
         
         for(OrderMenu m : orderTableOl) {
            addTableBill(m);
            System.out.println(m.getName());
            //$$�� ī�װ�/�̸�/���� �÷� ������ , @@�� �� ����
            msg += m.getName() + "$$" + m.getCnt() + "$$" + m.getTotalPrice();
            msg += "@@";
         }
         msg = msg.substring(0, msg.length() -2);
         send_Message("�ֹ�/////" + msg);
         Platform.runLater(() -> orderTableOl.clear());
         priceUpdate();
         
         
         }catch (Exception e) {
            return;
      }
         
      }
      
      private void addTableBill(OrderMenu m) {
         if(orderTableTotal.size() == 0) {
            orderTableTotal.add(m);//��꼭�� ������� �ֹ��ϴ� �޴��� �� �Է�
         }else {
            for(OrderMenu om : orderTableTotal) {
               if(om.getName().equals(m.getName())) {
                  om.setCnt(om.getCnt() + m.getCnt());
                  om.setTotalPrice(om.getTotalPrice() + m.getTotalPrice());
                  return;
               }
               orderTableTotal.add(m);
               return;
            }
         }
      }
      
      private void send_Message(String msg) {
         try {
            //������ ����
         dos.writeUTF(msg);
         
      } catch (Exception e) {
         e.printStackTrace();
      }

      }
      
   //���� üũ
      private boolean connCheck(String message) {
         StringTokenizer st = new StringTokenizer(message, "/");
         String protocol = st.nextToken();
//         String msg = st.nextToken();
         System.out.println("��������" + protocol);
         if(protocol.equals("connOk")) {
            return true;
         }else if(protocol.equals("connFail")) {
            return false;
         }
         return false;
      }
      
      private void callBill(ActionEvent event) {
    	  try {
    		  //��Ȯ�� ��꼭�� �ޱ����ؼ� pos�� ���� 
    		  //�������̺��� �����޴�����Ʈ ��û
    		  send_Message("��꼭///" + Integer.parseInt(this.tableNo.getText()));
    		  String msg = dis.readUTF();
    		  Bill b = new Bill();
    		  b.show(orderTableTotal, msg);
    		  
    	  }catch (Exception e) {
    		  e.printStackTrace();
		}
      }
      
      
      //'-' ��ư ����
      private void subtractBtnAction(ActionEvent event) {
         //���Ÿ�Ͽ� �ϳ������� �������ϱ� ����
         if(orderTableOl.size() == 0)
            return;
         //���Ÿ�Ͽ� �־ ���þ��ϰ� ������ �������ϱ� ����
         if(orderTable.getSelectionModel().getSelectedItem() == null)
                return;
         
         String name = orderTable.getSelectionModel().getSelectedItem().getName();
         
         //���Ÿ�Ͽ��� ������ �޴��� �̸����� ã�´�.
         for(OrderMenu om : orderTableOl) {
            if(om.getName().equals(name)) {
               if(om.getCnt() > 1)
                  om.setCnt(om.getCnt() - 1);
               else 
                  orderTableOl.remove(om);
               
               orderTable.refresh();
               priceUpdate();
               break;
            }
         }
         
      }
      //'+' ��ư ����
      private void plusBtnAction(ActionEvent event) {
         if(orderTableOl.size() == 0)
            return;
         if(orderTable.getSelectionModel().getSelectedItem() == null)
                return;
         
         String name = orderTable.getSelectionModel().getSelectedItem().getName();
         
         for(OrderMenu om : orderTableOl) {
            if(om.getName().equals(name)) {
               om.setCnt(om.getCnt() + 1);
               orderTable.refresh();
               priceUpdate();
            }
         }
      }
      
      private void priceUpdate() {
         Platform.runLater( () -> {
            int totalPrice = 0;
            for(OrderMenu m : orderTableOl) {
               totalPrice += m.getTotalPrice();
            }
            total.setText(totalPrice + "��");
            System.out.println(total.getText());
         });
      }
      
   
}