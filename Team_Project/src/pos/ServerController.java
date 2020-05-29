package pos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import pos.menu.Menu;
import pos.menu.MenuDAO;
import pos.tablepayment.TablePaymentController;

public class ServerController implements Initializable{
   
   Stage serverStage = ServerMain.serverStage;
   Client kitchen; //주방 클라이언트
   
   //POS FXML 멤버
   private @FXML TabPane tab;
   private @FXML BorderPane borderPane;
   private @FXML GridPane home;   //테이블이 있는 화면
   
   //table.fxml 멤버
   
   private Parent addMenu;         //메뉴 관리 화면
   private Parent receipt; //영수증 화면
   private Parent salesStatus; //판매현황 화면
   private MenuDAO dao;
   
//   private InetAddress ip;            //IP
   private ExecutorService threadPool;   //스레드풀
   private ServerSocket serverSocket;   //서버소켓
   private Socket socket;
   private List<Client> client_list = new ArrayList<>();
   private List<Parent> table_list = new ArrayList<>();
   private List<Menu> menu_list;
   
   private StringTokenizer st;
   private StringTokenizer st2;
   
   private Date time;
   private SimpleDateFormat format = new SimpleDateFormat( "hh시mm분ss초");
   private TablePaymentController tp;
   
   @Override
   public void initialize(URL location, ResourceBundle resources){
      try {
         //처음에 시작할 때 DB에서 서버로 메뉴를 받아온다.
        dao = MenuDAO.getinstance();
        menu_list = dao.selectAll();
        
        tp = new TablePaymentController(menu_list);
        
         addMenu = FXMLLoader.load(getClass().getResource("menu/menu.fxml"));
         receipt = FXMLLoader.load(getClass().getResource("management/Receipt.fxml"));
         salesStatus = FXMLLoader.load(getClass().getResource("management/SalesStatus.fxml"));
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      //오른쪽 탭 리스너
      tab.getSelectionModel().selectedItemProperty().addListener( (ob, oldT, newT) -> {
         System.out.println(newT.getText());
         String tab = newT.getText();
         if(tab.equals("AddMenu")){
            borderPane.setCenter(addMenu);
         }else if(tab.equals("Home")) {
            borderPane.setCenter(home);
         }else if(tab.equals("Receipt")) {
             borderPane.setCenter(receipt);
         }else if(tab.equals("SalesStatus")) {
            borderPane.setCenter(salesStatus);
         }   
      });
      
      //서버 시작
      startServer();
      serverStage.setOnCloseRequest(e -> stopServer());
      
      //처음에 테이블 초기화
      emptyTableInfo();
   }
   
   private void startServer() {
      try {
//         ip = InetAddress.getLocalHost();   //현재 컴퓨터 아이피 받아오기
         threadPool = Executors.newFixedThreadPool(20);
         if(serverSocket == null) {
            serverSocket = new ServerSocket();
//            serverSocket.bind(new InetSocketAddress(ip.getHostAddress(), 5555));
            serverSocket.bind(new InetSocketAddress("localhost", 8888));
            System.out.println("서버 시작");
         }
      } catch (Exception e) {
         stopServer();
      }
      //서버 시작 된 후 클라이언트의 연결을 기다린다.
      if(serverSocket != null) {
         Runnable runnable = new Runnable() {
            @Override
            public void run() {
               while(true) {
                  try {
                     socket = serverSocket.accept();
                     Client client = new Client(socket);
                     
                     
                  } catch (Exception e) {
                     stopServer();
                     break;
                  }
            
               }
            }
         };
         threadPool.execute(runnable);
      }
   }
   private void stopServer() {
      try {
         if(serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
         }
         if(threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
         }
         for(Client c : client_list) {
            c.socket.close();
         }
         if(kitchen != null && !kitchen.socket.isClosed()) {
            kitchen.socket.close();
         }
         Platform.exit();
         System.exit(0);
      }catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   private void emptyTableInfo() {
     for(int i=0; i<home.getRowConstraints().size(); i++) {
        for(int j=0; j<home.getColumnConstraints().size(); j++) {
           HBox empty = new HBox();
           empty.setAlignment(Pos.CENTER);
           Label emptyLabel = new Label(1+j+home.getColumnConstraints().size()*i + " 번 테이블 대기");
           empty.getChildren().add(emptyLabel);
           home.add(empty, j, i);
        }
     }
   }
   
   public class Client extends Thread{
      private int tableNo;         //테이블 번호
//      private List<Menu> menu_list = new ArrayList<>();   //각 테이블이 가질 메뉴 리스트 서버와 연동된다.
//      private List<OrderMenu> orderMenu_list = new ArrayList<>();
      //주문한 메뉴 담고있는 리스트
      public ObservableList<OrderMenu> orderMenu_list = FXCollections.observableArrayList();
      private Socket socket;         //서버에서 관리하는 각 테이블의 소켓
      
      private InputStream is;
      private OutputStream os;
      private DataInputStream dis;
      private DataOutputStream dos;

      private TableView<OrderMenu> tableView;
      private Label labelPrice;

      
      public Client(Socket socket) {
         this.socket = socket;
         client_Network();
      }
      
      private void client_Network() {
      try {
         is = socket.getInputStream();
         dis = new DataInputStream(is);
         os = socket.getOutputStream();
         dos = new DataOutputStream(os);
         
         //처음 들어올 때 테이블 넘버를 받는다.
         String tmp = dis.readUTF();
         //주방 연결 확인
         if(tmp.equals("주방")) {
            System.out.println("주방 맞다");
            kitchen = this;
            kitchen.start();
            return;
         }
         this.start();
         if(tmp != null) {
            tableNo = Integer.parseInt(tmp);
            //이미 접속해있는 테이블인지 확인.(번호 중복안되게)
            if(client_list.size() == 0) {
               dos.writeUTF("connOk/");
               System.out.println("처음 테이블 연결 확인 제발connOk ");
            }else if(client_list.size() != 0) {
               for(Client c : client_list) {
                  if(c.tableNo == tableNo) {
                     dos.writeUTF("connFail/");
                     System.out.println("중복 테이블 연결 확인 제발connFail ");
                     return;
                  }
               }
               System.out.println("마지막 확인 제발");
               dos.writeUTF("connOk/");
            }
            System.out.println(tableNo + "접속");
         }
         //테이블 연결
         makeTableInfo(tableNo);
         //접속되면 메뉴 정보를 전송
         String menu = "";
         for(Menu m : menu_list) {
            //$$는 메뉴번호/카테고리/이름/가격 컬럼 구분자 , @@는 행 구분
            menu += m.getMenuNum()+"$$"+ m.getCategory() + "$$" + m.getName() + "$$" + m.getPrice();
            menu += "@@";
         }
         System.out.println(menu);
         menu = menu.substring(0, menu.length()-2);
//         파스타$$알리오 올리오$$15000원@@
//         테이블에 메뉴 전송
         if(menu != null) {
            dos.writeUTF(menu);
         }
         client_list.add(this);
         
      }catch (Exception e) {
         e.printStackTrace();
      }
      }
      
      //테이블로 부터 주문이나 정보를 기다린다.
      @Override
      public void run() {
         Runnable runnable = new Runnable() {
            
            @Override
            public void run() {
               while(true) {
                  try {
                     String msg = dis.readUTF();
                     msgProcess(msg);
                  }catch (Exception e) {
                     continue;
                  }
               }
            }
         };
         threadPool.execute(runnable);
      }
      
      private void msgProcess(String msg) {
         //@@@@주문이 오면 테이블 번호, 주문내역을 받아 넘겨준다.
         st = new StringTokenizer(msg, "///");
         String protocol = st.nextToken();
         String message = st.nextToken();
         System.out.println("프로토콜 : " + protocol);
         System.out.println("메세지 : " + message);
         //주문일 때
         /////////주문
         if(protocol.equals("주문")) {
            st2 = new StringTokenizer(message, "@@");
            if(kitchen != null) {
               sendOrderInfo(message);
            }
            boolean flag = false;
            while(st2.hasMoreTokens()) {
               String menu = st2.nextToken();
               //주방으로 메뉴 전송
               st = new StringTokenizer(menu, "$$");
               String name = st.nextToken();
               int cnt = Integer.parseInt(st.nextToken());
               int price = Integer.parseInt(st.nextToken())/cnt;
               //첫 주문이 아닐 때
               if(orderMenu_list.size() != 0) {
                  for(OrderMenu m : orderMenu_list) {
                     if(m.getName().equals(name)) {
                        m.setCnt(m.getCnt() + cnt);
                        m.setPrice(price);
                        tableView.setItems(orderMenu_list);
                        tableView.refresh();
                        System.out.println("같은게 있을때" + m.getName());
                        flag = true;
                        break;
                     }
                  }
               }
               //같은 주문이 없을 때
               if(flag == false) {
                  OrderMenu om = new OrderMenu(name, cnt, price);
                  orderMenu_list.add(om);
                  tableView.setItems(orderMenu_list);
                  tableView.refresh();
                  System.out.println(om.getName());
                  System.out.println(om.getPrice());
               }else {
                  flag = false;
               }
               System.out.println("price" + price);
               priceUpdate();
               tp.priceUpdate();
            }
         }/////////주문
         //////////종료
         else if(protocol.equals("종료")) {
            if(!this.socket.isClosed()) {
               try {
                  this.socket.close();
               } catch (Exception e) {
                  e.printStackTrace();
               }
            }
            Iterator<Client> it = client_list.iterator();
            while(it.hasNext()) {
              Client c = it.next();
                if(this.tableNo == c.tableNo) {
                   client_list.remove(c);
                   break;
                }
            }
            for(int i=0; i<home.getChildren().size(); i++) {
               Parent p = (Parent)home.getChildren().get(i);
               if(p.getId() != null) {
                  if(p.getId().equals("테이블" + this.tableNo)) {
                     Platform.runLater( () -> {
                        try {
                           home.getChildren().remove(p);   
                        }catch (Exception e) {
                           e.printStackTrace();
                  }
                     });
                     return;
                  }
               }
               
            }
         }//////////종료
         ///////////계산서
         //계산서는 태블릿이 계산서를 호출할 때
         //pos가 가지고있는 클라이언트의 오더메뉴 리스트를 보낸다.
         //이유는 테이블 누르면 나오는 tablepayment에서 갯수를 조정했을수도
         //있기 때문에 tablepayment에서 조정한값은 pos만 가지고있어서
         else if(protocol.equals("계산서")) {
            for(Client c : client_list) {
               if(c.tableNo == Integer.parseInt(message)) {
                  String menu = "";
                  for(OrderMenu om : c.orderMenu_list) {
                     menu += om.getName() + "$$" + om.getCnt() + "$$" + om.getPrice();
                     menu += "@@";
                  }
                  try {
                     c.dos.writeUTF(menu);
                     System.out.println("보냄");
               } catch (Exception e) {
                  e.printStackTrace();
               }
               }
            }
         }//////////계산서
      }
      
      @SuppressWarnings("unchecked")
      private void makeTableInfo(int tableNo) {
         try {
            //한줄에 몇개 드갈지 나중에 유동적으로 할수 있게
            int columnMax = home.getColumnConstraints().size();
            
            int row = (tableNo-1)/columnMax;
            int column = (tableNo-1)%columnMax;
            Parent table = FXMLLoader.load(getClass().getResource("table.fxml"));
           
            
            table.setId("테이블" + tableNo);
            tableView = (TableView<OrderMenu>)table.lookup("#tableView");
            labelPrice = (Label)table.lookup("#labelPrice");
            
            TableColumn<OrderMenu, ?> a = tableView.getColumns().get(0);
            a.setCellValueFactory(new PropertyValueFactory<>("name"));
            a.setText("테이블" + tableNo);
             
            TableColumn<OrderMenu, ?> b = tableView.getColumns().get(1);
            b.setCellValueFactory(new PropertyValueFactory<>("cnt"));
            b.setText("");
            
            //테이블 마우스 왼쪽 클릭시 결제(주문)창 뜨기 
            tableView.setOnMouseClicked(e -> {
               if(e.getButton() == MouseButton.PRIMARY) {
            	   tp.show(this.tableNo, this, tableView);
               }
            });
            
            //칼럼과 로우 맞춰서 테이블정보 넣기
            Platform.runLater(() -> {
//            home.getChildren().remove(tableNo-1);
            home.add(table, column, row);
            });
            table_list.add(table);
            
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
      
      public void priceUpdate() {
         Platform.runLater( () -> {
            int t = 0;
            for(OrderMenu om : orderMenu_list) {
               t += om.getTotal();
               System.out.println(om.getTotal());
               System.out.println(tableNo  + "에서" + t);
            }
            labelPrice.setText(t + "원");
         });
      }
      
      //테이블 번호, 주문내역전송
      private void sendOrderInfo(String menu) {
         time = new Date();
         String nowTime = format.format(time);
         //주방으로 메뉴 보냄
         try {
            kitchen.dos.writeUTF(tableNo+"///" + nowTime + "///" +menu);
            System.out.println("주방으로 보내는 메뉴: " + menu);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
   }
}