package tablet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
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
   
   
   //table.fxml
   private @FXML ListView<HBox> lvSalad;
   private ObservableList<HBox> saladOl = FXCollections.observableArrayList();
   private @FXML ListView<HBox> lvPasta;
   private ObservableList<HBox> PastaOl = FXCollections.observableArrayList();
   private @FXML TableView<OrderMenu> orderTable;
   private ObservableList<OrderMenu> orderTableOl = FXCollections.observableArrayList();
   private @FXML Button orderBtn;
   private @FXML Label total;
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
      tableSet();
      orderTable.setItems(orderTableOl);
      orderTable.setPlaceholder(new Label(""));
      
      orderTableOl.addListener(new ListChangeListener<OrderMenu>() {
    	  @Override
    	public void onChanged(Change<? extends OrderMenu> c) {
    		  int totalPrice = 0;
    		  for(OrderMenu m : c.getList()) {
    			  totalPrice += m.getTotalPrice();
    		  }
    		  total.setText(totalPrice + "원");
    	}
      });
      
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
            while(st1.hasMoreTokens()) {
               String tmp = st1.nextToken();
               st2 = new StringTokenizer(tmp, "$$");
               
               //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@수정필요
               menuList.add(new Menu(0,st2.nextToken(), st2.nextToken(), st2.nextToken()));
            }
            //메뉴들을 각 메뉴판에 담기
            for(Menu m : menuList) {
               if(m.getCategory().equals("파스타")) {
                  PastaOl = replaceMenu(PastaOl, m);
               }else if(m.getCategory().equals("샐러드")) {
                  saladOl = replaceMenu(saladOl, m);
               }
               //리스트뷰에 observablelist 연동
            }
            lvSalad.setItems(saladOl);
            lvPasta.setItems(PastaOl);
            
            orderTableSetting();
            
         }else {
            throw new Exception(); 
         }
      }catch (Exception e) {
         System.out.println("메뉴 정보를 받아오지 못함.");
         e.printStackTrace();
      }
   }
   
   //메뉴판에 메뉴를 넣는 메서드
      private ObservableList<HBox> replaceMenu(ObservableList<HBox> ol, Menu m){
         ObservableList<HBox> tempOl = ol;
         try {
            //각 메뉴 아이템
            Parent node = FXMLLoader.load(getClass().getResource("menuItem.fxml"));
            Label labelName = (Label)node.lookup("#labelName");
            Label labelPrice = (Label)node.lookup("#labelPrice");
            labelName.setText(m.getName());
            labelPrice.setText(m.getPrice());
            node.setOnMouseClicked(e -> {
               if(e.getClickCount() == 2) {
                  System.out.println("메뉴이름 : " + labelName.getText() + "메뉴가격 : " + labelPrice.getText());
                  addOrdertable(labelName.getText());
               }
            });
            if(tempOl.size() == 0) {
               HBox hbox = new HBox();
               hbox.setSpacing(10);
               hbox.getChildren().add(node);
               tempOl.add(hbox);
            }else if(tempOl.get(tempOl.size()-1).getChildren().size() % 3 == 0 ) {
               HBox hbox = new HBox();
               hbox.setSpacing(10);
               hbox.getChildren().add(node);
               tempOl.add(hbox);
            }else {
               tempOl.get(tempOl.size()-1).getChildren().add(node);
            }
            //마우스 더블클릭 액션
            
         }catch (Exception e) {
         }
         return tempOl;
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
               om.setTotalPrice(om.getCnt() * Integer.parseInt(om.getPrice()));
               int idx = orderTableOl.indexOf(om);
               OrderMenu om2 = om;
               orderTableOl.remove(om);
               orderTableOl.add(idx, om2);
               orderTable.refresh();
               return;
            }
         }
         OrderMenu om = new OrderMenu(mTmp.getName(), 1, mTmp.getPrice());
         orderTableOl.add(om);
         orderTable.refresh();
         }catch (Exception e) {
            e.printStackTrace();
         }
         
      }
   
      //'주문하기'버튼의 액션
      private void orderBtnAction(ActionEvent event) {
    	  try {
    	  String msg = "";
    	  System.out.println();
    	  for(OrderMenu m : orderTableOl) {
    		  //$$는 카테고리/이름/가격 컬럼 구분자 , @@는 행 구분
    		  msg += m.getName() + "$$" + m.getCnt() + "$$" + m.getTotalPrice();
    		  msg += "@@";
    	  }
    	  msg = msg.substring(0, msg.length() -2);
    	  send_Message("주문/////" + msg);
    	  System.out.println(msg);
    	  }catch (Exception e) {
    		  return;
		}
      }
      
      private void send_Message(String msg) {
    	  try {
    		  //서버로 전송
			dos.writeUTF(msg);
			Platform.runLater(() -> orderTableOl.clear());
			
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
      
   
}