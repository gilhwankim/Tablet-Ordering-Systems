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
   Client kitchen; //�ֹ� Ŭ���̾�Ʈ
   
   //POS FXML ���
   private @FXML TabPane tab;
   private @FXML BorderPane borderPane;
   private @FXML GridPane home;   //���̺��� �ִ� ȭ��
   
   //table.fxml ���
   
   private Parent addMenu;         //�޴� ���� ȭ��
   private Parent receipt; //������ ȭ��
   private Parent salesStatus; //�Ǹ���Ȳ ȭ��
   private MenuDAO dao;
   
//   private InetAddress ip;            //IP
   private ExecutorService threadPool;   //������Ǯ
   private ServerSocket serverSocket;   //��������
   private Socket socket;
   private List<Client> client_list = new ArrayList<>();
   private List<Parent> table_list = new ArrayList<>();
   private List<Menu> menu_list;
   
   private StringTokenizer st;
   private StringTokenizer st2;
   
   private Date time;
   private SimpleDateFormat format = new SimpleDateFormat( "hh��mm��ss��");
   private TablePaymentController tp;
   
   @Override
   public void initialize(URL location, ResourceBundle resources){
      try {
         //ó���� ������ �� DB���� ������ �޴��� �޾ƿ´�.
        dao = MenuDAO.getinstance();
        menu_list = dao.selectAll();
        
        tp = new TablePaymentController(menu_list);
        
         addMenu = FXMLLoader.load(getClass().getResource("menu/menu.fxml"));
         receipt = FXMLLoader.load(getClass().getResource("management/Receipt.fxml"));
         salesStatus = FXMLLoader.load(getClass().getResource("management/SalesStatus.fxml"));
      } catch (Exception e) {
         e.printStackTrace();
      }
      
      //������ �� ������
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
      
      //���� ����
      startServer();
      serverStage.setOnCloseRequest(e -> stopServer());
      
      //ó���� ���̺� �ʱ�ȭ
      emptyTableInfo();
   }
   
   private void startServer() {
      try {
//         ip = InetAddress.getLocalHost();   //���� ��ǻ�� ������ �޾ƿ���
         threadPool = Executors.newFixedThreadPool(20);
         if(serverSocket == null) {
            serverSocket = new ServerSocket();
//            serverSocket.bind(new InetSocketAddress(ip.getHostAddress(), 5555));
            serverSocket.bind(new InetSocketAddress("localhost", 8888));
            System.out.println("���� ����");
         }
      } catch (Exception e) {
         stopServer();
      }
      //���� ���� �� �� Ŭ���̾�Ʈ�� ������ ��ٸ���.
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
           Label emptyLabel = new Label(1+j+home.getColumnConstraints().size()*i + " �� ���̺� ���");
           empty.getChildren().add(emptyLabel);
           home.add(empty, j, i);
        }
     }
   }
   
   public class Client extends Thread{
      private int tableNo;         //���̺� ��ȣ
//      private List<Menu> menu_list = new ArrayList<>();   //�� ���̺��� ���� �޴� ����Ʈ ������ �����ȴ�.
//      private List<OrderMenu> orderMenu_list = new ArrayList<>();
      //�ֹ��� �޴� ����ִ� ����Ʈ
      public ObservableList<OrderMenu> orderMenu_list = FXCollections.observableArrayList();
      private Socket socket;         //�������� �����ϴ� �� ���̺��� ����
      
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
         
         //ó�� ���� �� ���̺� �ѹ��� �޴´�.
         String tmp = dis.readUTF();
         //�ֹ� ���� Ȯ��
         if(tmp.equals("�ֹ�")) {
            System.out.println("�ֹ� �´�");
            kitchen = this;
            kitchen.start();
            return;
         }
         this.start();
         if(tmp != null) {
            tableNo = Integer.parseInt(tmp);
            //�̹� �������ִ� ���̺����� Ȯ��.(��ȣ �ߺ��ȵǰ�)
            if(client_list.size() == 0) {
               dos.writeUTF("connOk/");
               System.out.println("ó�� ���̺� ���� Ȯ�� ����connOk ");
            }else if(client_list.size() != 0) {
               for(Client c : client_list) {
                  if(c.tableNo == tableNo) {
                     dos.writeUTF("connFail/");
                     System.out.println("�ߺ� ���̺� ���� Ȯ�� ����connFail ");
                     return;
                  }
               }
               System.out.println("������ Ȯ�� ����");
               dos.writeUTF("connOk/");
            }
            System.out.println(tableNo + "����");
         }
         //���̺� ����
         makeTableInfo(tableNo);
         //���ӵǸ� �޴� ������ ����
         String menu = "";
         for(Menu m : menu_list) {
            //$$�� �޴���ȣ/ī�װ�/�̸�/���� �÷� ������ , @@�� �� ����
            menu += m.getMenuNum()+"$$"+ m.getCategory() + "$$" + m.getName() + "$$" + m.getPrice();
            menu += "@@";
         }
         System.out.println(menu);
         menu = menu.substring(0, menu.length()-2);
//         �Ľ�Ÿ$$�˸��� �ø���$$15000��@@
//         ���̺� �޴� ����
         if(menu != null) {
            dos.writeUTF(menu);
         }
         client_list.add(this);
         
      }catch (Exception e) {
         e.printStackTrace();
      }
      }
      
      //���̺�� ���� �ֹ��̳� ������ ��ٸ���.
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
         //@@@@�ֹ��� ���� ���̺� ��ȣ, �ֹ������� �޾� �Ѱ��ش�.
         st = new StringTokenizer(msg, "///");
         String protocol = st.nextToken();
         String message = st.nextToken();
         System.out.println("�������� : " + protocol);
         System.out.println("�޼��� : " + message);
         //�ֹ��� ��
         /////////�ֹ�
         if(protocol.equals("�ֹ�")) {
            st2 = new StringTokenizer(message, "@@");
            if(kitchen != null) {
               sendOrderInfo(message);
            }
            boolean flag = false;
            while(st2.hasMoreTokens()) {
               String menu = st2.nextToken();
               //�ֹ����� �޴� ����
               st = new StringTokenizer(menu, "$$");
               String name = st.nextToken();
               int cnt = Integer.parseInt(st.nextToken());
               int price = Integer.parseInt(st.nextToken())/cnt;
               //ù �ֹ��� �ƴ� ��
               if(orderMenu_list.size() != 0) {
                  for(OrderMenu m : orderMenu_list) {
                     if(m.getName().equals(name)) {
                        m.setCnt(m.getCnt() + cnt);
                        m.setPrice(price);
                        tableView.setItems(orderMenu_list);
                        tableView.refresh();
                        System.out.println("������ ������" + m.getName());
                        flag = true;
                        break;
                     }
                  }
               }
               //���� �ֹ��� ���� ��
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
         }/////////�ֹ�
         //////////����
         else if(protocol.equals("����")) {
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
                  if(p.getId().equals("���̺�" + this.tableNo)) {
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
         }//////////����
         ///////////��꼭
         //��꼭�� �º��� ��꼭�� ȣ���� ��
         //pos�� �������ִ� Ŭ���̾�Ʈ�� �����޴� ����Ʈ�� ������.
         //������ ���̺� ������ ������ tablepayment���� ������ ������������
         //�ֱ� ������ tablepayment���� �����Ѱ��� pos�� �������־
         else if(protocol.equals("��꼭")) {
            for(Client c : client_list) {
               if(c.tableNo == Integer.parseInt(message)) {
                  String menu = "";
                  for(OrderMenu om : c.orderMenu_list) {
                     menu += om.getName() + "$$" + om.getCnt() + "$$" + om.getPrice();
                     menu += "@@";
                  }
                  try {
                     c.dos.writeUTF(menu);
                     System.out.println("����");
               } catch (Exception e) {
                  e.printStackTrace();
               }
               }
            }
         }//////////��꼭
      }
      
      @SuppressWarnings("unchecked")
      private void makeTableInfo(int tableNo) {
         try {
            //���ٿ� � �尥�� ���߿� ���������� �Ҽ� �ְ�
            int columnMax = home.getColumnConstraints().size();
            
            int row = (tableNo-1)/columnMax;
            int column = (tableNo-1)%columnMax;
            Parent table = FXMLLoader.load(getClass().getResource("table.fxml"));
           
            
            table.setId("���̺�" + tableNo);
            tableView = (TableView<OrderMenu>)table.lookup("#tableView");
            labelPrice = (Label)table.lookup("#labelPrice");
            
            TableColumn<OrderMenu, ?> a = tableView.getColumns().get(0);
            a.setCellValueFactory(new PropertyValueFactory<>("name"));
            a.setText("���̺�" + tableNo);
             
            TableColumn<OrderMenu, ?> b = tableView.getColumns().get(1);
            b.setCellValueFactory(new PropertyValueFactory<>("cnt"));
            b.setText("");
            
            //���̺� ���콺 ���� Ŭ���� ����(�ֹ�)â �߱� 
            tableView.setOnMouseClicked(e -> {
               if(e.getButton() == MouseButton.PRIMARY) {
            	   tp.show(this.tableNo, this, tableView);
               }
            });
            
            //Į���� �ο� ���缭 ���̺����� �ֱ�
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
               System.out.println(tableNo  + "����" + t);
            }
            labelPrice.setText(t + "��");
         });
      }
      
      //���̺� ��ȣ, �ֹ���������
      private void sendOrderInfo(String menu) {
         time = new Date();
         String nowTime = format.format(time);
         //�ֹ����� �޴� ����
         try {
            kitchen.dos.writeUTF(tableNo+"///" + nowTime + "///" +menu);
            System.out.println("�ֹ����� ������ �޴�: " + menu);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      
   }
}