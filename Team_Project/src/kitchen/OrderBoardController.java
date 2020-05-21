package kitchen;

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
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class OrderBoardController implements Initializable {
   
      //서버연결에 필요한 멤버
      private Socket socket;
      private InputStream is;
      private OutputStream os;
      private DataInputStream dis;
      private DataOutputStream dos;
      
      StringTokenizer st;
      StringTokenizer st2;
      StringTokenizer st3;
      
      private @FXML Label tableNum; //테이블 번호 라벨 
      private TableView<OrderBoardMenu> kitchenTableview; //테이블 뷰
      
      private @FXML ListView<HBox> orderBoardlv; //리스트뷰
      private ObservableList<HBox> tableViewOl = FXCollections.observableArrayList();
      
      private List<OrderBoardMenu> menuList = new ArrayList<>();
      private OrderBoardMenu obm;
      private int cnt = 0;
      
//      private String tableNumber;
//      private String menuName;
//      private String menuCnt ;
//      private String menuPrice ;
      
   @Override
   public void initialize(URL location, ResourceBundle resources) {
      startClient();
   }
   public OrderBoardController() {
      
   }
   
     private void startClient() {
         try {
           //서버 연결
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 8888));
            //네트워크 자원 할당
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            //서버에게 주방을 알린다.
            dos.writeUTF("주방");
            
            System.out.println("연결성공!");
            //성공
            temp();
            
      }catch (Exception e) {
         if(!socket.isClosed())
            try {
               is.close();
               dis.close();
               os.close();
               dos.close();
               socket.close();
            }catch (Exception e2) {
               e2.printStackTrace();
            }
      }
         
     }
     
     //연결이 성공하면, 서버로부터 테이블들의 주문을 전달받는다.
     public void temp() {
        Thread thread = new Thread(new Runnable() {
             @Override
             public void run() {
                while(true) {
                   try {
                      String message = dis.readUTF();
                      
//                      System.out.println("주방에서 받은거 다: " + message);
                      //(테이블번호///메뉴이름$$수량$$가격@@메뉴이름$$수량$$가격)
                      //테이블 번호와 메뉴를 나눈다.
                      st = new StringTokenizer(message,"///");
                      String tableNumber = st.nextToken();
                      String time = st.nextToken();
                      String allMenu = st.nextToken();
                      
//                      System.out.println("주방에서 받은 allMenu: "+allMenu);
                      //메뉴별로 나눈다(@@)
                      st2= new StringTokenizer(allMenu,"@@");

                      while(st2.hasMoreTokens()) {
                         String temp = st2.nextToken();
                         //메뉴의 이름$$수량$$가격 으로 나눈다.
                         st3 = new StringTokenizer(temp,"$$");
                         obm = new OrderBoardMenu(st3.nextToken(), st3.nextToken());
                         st3.nextToken();   //가격인데 필요없으므로 흘린다.
                         
                         
   //                      menuName = st3.nextToken();
   //                      menuCnt = st3.nextToken();
   //                      menuPrice = st3.nextToken();
                         
   //                      System.out.println("테이블 번호: " + tableNumber);
   //                      System.out.println("메뉴 이름: " + menuName);
   //                      System.out.println("메뉴 수량: " + menuCnt);
   //                      System.out.println("메뉴 가격: " + menuPrice);
                         
                         //메뉴담을 리스트에 메뉴 객체(이름,수량)을 만들어 넣는다.
                         menuList.add(obm);
                      }
                      
                      //오더보드에 메뉴내역 추가
                      ordertoBoard(tableNumber, time);
                      //오더보드 리스트에 tableViewOl연동
                      orderBoardlv.setItems(tableViewOl);
                      orderBoardlv.refresh();
                      //테이블 뷰 초기화
                      orderTableSettingg(tableNumber);
                      menuList.clear();
                      
                   } catch (IOException e) {
                      e.printStackTrace();
                   }   
                }
                
             }
          });
        thread.start();
     }
     
        //오더들어오면 HBox만들어서 오더보드에 추가하는 메서드
      @SuppressWarnings("unchecked")
      private void ordertoBoard(String tableNum, String time) {
            try {
               //오더 들어갈 테이블 뷰 fxml 
               Parent node = FXMLLoader.load(getClass().getResource("OrderMenu.fxml"));
               Label fxtableNum = (Label)node.lookup("#time");
               Button orderCom = (Button)node.lookup("#orderCom");
               kitchenTableview = (TableView<OrderBoardMenu>)node.lookup("#kitchenTableview");
               
               //주문마다 적혀있는 테이블 번호
               fxtableNum.setText(time);
               //확인버튼 액션
               orderCom.setOnAction(e -> orderComAction(e));
               ObservableList<OrderBoardMenu> menuToTable = FXCollections.observableArrayList(); 
               
               for(OrderBoardMenu m : menuList) {
                  menuToTable.add(m);   
               }
               //버튼마다 카운트를 준다.
               orderCom.setId(orderCom.getId() + cnt++);
               
               kitchenTableview.setItems(menuToTable);
               if(tableViewOl.size() == 0) {
                        HBox hbox = new HBox();
                        hbox.setSpacing(10);
                        hbox.getChildren().add(node);
                        tableViewOl.add(hbox);
                     }else if(tableViewOl.get(tableViewOl.size()-1).getChildren().size() % 4 == 0 ) {
                        HBox hbox = new HBox();
                        hbox.setSpacing(10);
                        hbox.getChildren().add(node);
                        Platform.runLater(()-> tableViewOl.add(hbox));
                     }else {
                        Platform.runLater(()->tableViewOl.get(tableViewOl.size()-1).getChildren().add(node));
                     }
                  }catch (Exception e) {
                     e.printStackTrace();
                  }
      }
      //테이블 뷰 데이터 형식 지정
         private void orderTableSettingg(String tableNumber) {
             TableColumn<OrderBoardMenu, ?> a = kitchenTableview.getColumns().get(0);
              a.setCellValueFactory(new PropertyValueFactory<>("menuName"));
              a.setText("테이블 번호 : " + tableNumber);
              
              TableColumn<OrderBoardMenu, ?> b = kitchenTableview.getColumns().get(1);
              b.setCellValueFactory(new PropertyValueFactory<>("menuCnt"));
              b.setText("");
         }
         
         //완료버튼 눌렀을 때 테이블 삭제
         private void orderComAction(ActionEvent event) {
            //리스트에서 hbox를 부른다
            for(HBox h : tableViewOl) {
               //hbox에 node 들을 하나씩 골라 버튼의 이름을 확인
               for(int i=0; i<h.getChildren().size(); i++) {
                  AnchorPane n = (AnchorPane)h.getChildren().get(i);
                  Button b = (Button)n.getChildren().get(2);
                  //맞으면 해당 node 삭제
                  if(event.getTarget().toString().indexOf(b.getId()) != -1) {
                     System.out.println("버튼" + b.getId());
                     h.getChildren().remove(i);
                     //HBox에 노드가 한개도 없으면 Hbox 삭제
                     if(h.getChildren().size() == 0) {
                        tableViewOl.remove(h);
                     }
                     return;
                  }
               }
            }
         }
}