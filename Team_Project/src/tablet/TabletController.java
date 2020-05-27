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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
   
   
   //table.fxml 카테고리 탭
   private @FXML ListView<HBox> lvSalad;
   private ObservableList<HBox> saladOl = FXCollections.observableArrayList();
   private @FXML ListView<HBox> lvPasta;
   private ObservableList<HBox> PastaOl = FXCollections.observableArrayList();
   private @FXML ListView<HBox> lvSteak;
   private ObservableList<HBox> SteakOl = FXCollections.observableArrayList();
   private @FXML ListView<HBox> lvpilaf;
   private ObservableList<HBox> PilafOl = FXCollections.observableArrayList();
   private @FXML ListView<HBox> lvPizza;
   private ObservableList<HBox> PizzaOl = FXCollections.observableArrayList();
   private @FXML ListView<HBox> lvDrink;
   private ObservableList<HBox> DrinkOl = FXCollections.observableArrayList();
   private @FXML ListView<HBox> lvAlcohol;
   private ObservableList<HBox> AlcoholOl = FXCollections.observableArrayList();
   private @FXML ListView<HBox> lvetc;
   private ObservableList<HBox> EtcOl = FXCollections.observableArrayList();
   
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
            //메뉴들을 각 메뉴판에 담기
            for(Menu m : menuList) {
               if(m.getCategory().equals("파스타")) {
                  PastaOl = replaceMenu(PastaOl, m);
               }else if(m.getCategory().equals("샐러드")) {
                  saladOl = replaceMenu(saladOl, m);
               }else if(m.getCategory().equals("스테이크")) {
                 SteakOl = replaceMenu(SteakOl, m);
               }else if(m.getCategory().equals("필라프")) {
                 PilafOl = replaceMenu(PilafOl,m);
               }else if(m.getCategory().equals("피자")) {
                 PizzaOl = replaceMenu(PizzaOl,m);
               }else if(m.getCategory().equals("음료")) {
                 DrinkOl = replaceMenu(DrinkOl,m);
               }else if(m.getCategory().equals("주류")) {
                 AlcoholOl = replaceMenu(AlcoholOl,m);
               }else if(m.getCategory().equals("기타")) {
                 EtcOl = replaceMenu(EtcOl,m);
               }
               //리스트뷰에 observablelist 연동
            }
            lvSalad.setItems(saladOl);
            lvPasta.setItems(PastaOl);
            lvSteak.setItems(SteakOl);
            lvpilaf.setItems(PilafOl);
            lvPizza.setItems(PizzaOl);
            lvDrink.setItems(DrinkOl);
            lvAlcohol.setItems(AlcoholOl);
            lvetc.setItems(EtcOl);
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
      private ObservableList<HBox> replaceMenu(ObservableList<HBox> ol, Menu m){
         ObservableList<HBox> tempOl = ol;
         try {
            //각 메뉴 아이템
            Parent node = FXMLLoader.load(getClass().getResource("menuItem.fxml"));
            Label labelName = (Label)node.lookup("#labelName");
            Label labelPrice = (Label)node.lookup("#labelPrice");
            //menuItem.fxml에서 imageView 찾아옴
            ImageView imageMenu = (ImageView)node.lookup("#menuImg");
            //메뉴이름과 같은 이미지를 띄워줌
            imageMenu.setImage(new Image(getClass().getResource(
                    "/images/" + m.getName() + ".jpg").toString()));                 
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
         System.out.println();
         
         for(OrderMenu m : orderTableOl) {
            addTableBill(m);
            System.out.println(m.getName());
            //$$는 카테고리/이름/가격 컬럼 구분자 , @@는 행 구분
            msg += m.getName() + "$$" + m.getCnt() + "$$" + m.getTotalPrice();
            msg += "@@";
         }
         msg = msg.substring(0, msg.length() -2);
         send_Message("주문/////" + msg);
         System.out.println(msg);
         Platform.runLater(() -> orderTableOl.clear());
         priceUpdate();
         
         
         }catch (Exception e) {
            e.printStackTrace();
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
      
      //테이블별 계산서 부르는 메서드
      private void callBill(ActionEvent event) {          
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
                
                //정확한 계산서를 받기위해서 pos로 부터 
                //현재테이블의 오더메뉴리스트 요청
                send_Message("계산서///" + Integer.parseInt(this.tableNo.getText()));
                String msg = dis.readUTF();
                System.out.println(msg);

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
             } catch (IOException e) { e.printStackTrace(); }      
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
      //'+' 버튼 동장
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