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
   
   //처음 자리정하는 창
   private Button btn;
   private ChoiceBox<String> cb;
   private ObservableList<String> ol = FXCollections.observableArrayList();
   
   //서버연결에 필요한 멤버
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
 //테이블에서 주문한 전체 리스트
   private ObservableList<OrderMenu> orderTableTotal = FXCollections.observableArrayList();
   private @FXML Button orderBtn;
   private @FXML Label total; 
   private @FXML Label tableNo; //태플릿에 테이블번호 표시 라벨
   private @FXML Button billBtn; //계산서 호출 버튼
   private @FXML Button subtractBtn; // - 버튼
   private @FXML Button plusBtn; // + 버튼
   private @FXML TabPane tp;
   
   
   
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
      //종료버튼
     clientStage.setOnCloseRequest(e -> stopClient());
      
      tableSet();
      orderTable.setItems(orderTableOl);
      orderTable.setPlaceholder(new Label(""));
      
      billBtn.setOnAction((event)-> callBill(event)); //계산서 버튼 메서드
      plusBtn.setOnAction( e -> plusBtnAction(e));
      subtractBtn.setOnAction( e -> subtractBtnAction(e));
      orderBtn.setOnAction(e -> orderBtnAction(e));
      
   }
   
   //처음 올라오는 자리정하는 창
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
         if(Integer.parseInt(tableNo)<10) { //테이블 번호 10이하면 0 + 테이블 번호
            this.tableNo.setText("0" + tableNo);             
         }else {
            this.tableNo.setText(tableNo); //10이상은 그대로 출력
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
         //접속하고 테이블 번호를 서버에 전송
         dos.writeUTF(tableNo);
         
         //내이름과 같은 테이블이 있는지 서버에 체크 (성공하면 connOk, 실패시 connFail)
         String message = dis.readUTF();
         
         //false가 나오면 리턴시켜 다시 자리정하게한다.
         if(!connCheck(message)) {
            return;
         }
         stage.close();
         clientStage.show();
         
         //메뉴를 서버로부터 받는다.
         String menu = dis.readUTF();
         //메뉴를 받아오는데에 성공했을 때
         if(menu != null) {
            st1 = new StringTokenizer(menu, "@@");
            System.out.println("서버에서 전체 받아온 메뉴" + menu);
            while(st1.hasMoreTokens()) {
               String tmp = st1.nextToken();
               st2 = new StringTokenizer(tmp, "$$");
               menuList.add(new Menu(Integer.parseInt(st2.nextToken()),st2.nextToken(), st2.nextToken(), st2.nextToken()));
            }
            
         for(Menu m : menuList) {
            System.out.println("메뉴리스트 확인:"+m.getMenuNum()+","+m.getCategory()+","+m.getName()+","+m.getPrice()+",");
         }
         
         	MakeTab mt = new MakeTab();
         	tp = mt.make(menuList, tp);
         	addMenu();
            orderTableSetting();
            
         }else {
            throw new Exception(); 
         }
      }catch (Exception e) {
         System.out.println("메뉴 정보를 받아오지 못함.");
         e.printStackTrace();
      }
   }
   
   private void stopClient() {
         try {
            dos.writeUTF("종료///태블릿");
            dos.flush();
            if(!socket.isClosed()) {
                 socket.close();
               }
            is.close();
               dis.close();
               os.close();
               dos.close();
               System.out.println("태블릿 종료.");
               Platform.exit();
               System.exit(0);
              }catch (Exception e) {
                 System.exit(0);
              }
      }
   
   //메뉴판에 메뉴를 넣는 메서드
    @SuppressWarnings("unchecked")
	private void addMenu(){
    	  for(Menu m : menuList) {
	         try {
	            //각 메뉴 아이템
	            VBox node = FXMLLoader.load(getClass().getResource("menuItem.fxml"));
	            Label labelName = (Label)node.lookup("#labelName");
	            Label labelPrice = (Label)node.lookup("#labelPrice");
	            //menuItem.fxml에서 imageView 찾아옴
	            ImageView imageMenu = (ImageView)node.lookup("#menuImg");
	            
	            try {
	            	 //메뉴이름과 같은 이미지를 띄워줌
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
	                  System.out.println("메뉴이름 : " + labelName.getText() + "메뉴가격 : " + labelPrice.getText());
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
      
      //테이블뷰 초기화
      private void orderTableSetting() {
         TableColumn<OrderMenu, ?> a = orderTable.getColumns().get(0);
          a.setCellValueFactory(new PropertyValueFactory<>("name"));
          a.setText("메뉴 명");
          
          TableColumn<OrderMenu, ?> b = orderTable.getColumns().get(1);
          b.setCellValueFactory(new PropertyValueFactory<>("cnt"));
          b.setText("수량");
          
          TableColumn<OrderMenu, ?> c = orderTable.getColumns().get(2);
          c.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
          c.setText("가격");
          
      }
      
      //구매 테이블에 메뉴 넣기.
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
   
      //'주문하기'버튼의 액션
      private void orderBtnAction(ActionEvent event) {
         try {
         String msg = "";
         
         for(OrderMenu m : orderTableOl) {
            addTableBill(m);
            System.out.println(m.getName());
            //$$는 카테고리/이름/가격 컬럼 구분자 , @@는 행 구분
            msg += m.getName() + "$$" + m.getCnt() + "$$" + m.getTotalPrice();
            msg += "@@";
         }
         msg = msg.substring(0, msg.length() -2);
         send_Message("주문/////" + msg);
         Platform.runLater(() -> orderTableOl.clear());
         priceUpdate();
         
         
         }catch (Exception e) {
            return;
      }
         
      }
      
      private void addTableBill(OrderMenu m) {
         if(orderTableTotal.size() == 0) {
            orderTableTotal.add(m);//계산서에 현재까지 주문하는 메뉴를 다 입력
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
            //서버로 전송
         dos.writeUTF(msg);
         
      } catch (Exception e) {
         e.printStackTrace();
      }

      }
      
   //접속 체크
      private boolean connCheck(String message) {
         StringTokenizer st = new StringTokenizer(message, "/");
         String protocol = st.nextToken();
//         String msg = st.nextToken();
         System.out.println("프로토콜" + protocol);
         if(protocol.equals("connOk")) {
            return true;
         }else if(protocol.equals("connFail")) {
            return false;
         }
         return false;
      }
      
      private void callBill(ActionEvent event) {
    	  try {
    		  //정확한 계산서를 받기위해서 pos로 부터 
    		  //현재테이블의 오더메뉴리스트 요청
    		  send_Message("계산서///" + Integer.parseInt(this.tableNo.getText()));
    		  String msg = dis.readUTF();
    		  Bill b = new Bill();
    		  b.show(orderTableTotal, msg);
    		  
    	  }catch (Exception e) {
    		  e.printStackTrace();
		}
      }
      
      
      //'-' 버튼 동작
      private void subtractBtnAction(ActionEvent event) {
         //구매목록에 하나없으면 에러나니까 리턴
         if(orderTableOl.size() == 0)
            return;
         //구매목록에 있어도 선택안하고 누르면 에러나니까 리턴
         if(orderTable.getSelectionModel().getSelectedItem() == null)
                return;
         
         String name = orderTable.getSelectionModel().getSelectedItem().getName();
         
         //구매목록에서 선택한 메뉴를 이름으로 찾는다.
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
      //'+' 버튼 동작
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
            total.setText(totalPrice + "원");
            System.out.println(total.getText());
         });
      }
      
   
}