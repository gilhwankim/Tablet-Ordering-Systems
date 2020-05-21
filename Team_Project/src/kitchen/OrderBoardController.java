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
   
      //�������ῡ �ʿ��� ���
      private Socket socket;
      private InputStream is;
      private OutputStream os;
      private DataInputStream dis;
      private DataOutputStream dos;
      
      StringTokenizer st;
      StringTokenizer st2;
      StringTokenizer st3;
      
      private @FXML Label tableNum; //���̺� ��ȣ �� 
      private TableView<OrderBoardMenu> kitchenTableview; //���̺� ��
      
      private @FXML ListView<HBox> orderBoardlv; //����Ʈ��
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
           //���� ����
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 8888));
            //��Ʈ��ũ �ڿ� �Ҵ�
            is = socket.getInputStream();
            dis = new DataInputStream(is);
            os = socket.getOutputStream();
            dos = new DataOutputStream(os);
            //�������� �ֹ��� �˸���.
            dos.writeUTF("�ֹ�");
            
            System.out.println("���Ἲ��!");
            //����
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
     
     //������ �����ϸ�, �����κ��� ���̺���� �ֹ��� ���޹޴´�.
     public void temp() {
        Thread thread = new Thread(new Runnable() {
             @Override
             public void run() {
                while(true) {
                   try {
                      String message = dis.readUTF();
                      
//                      System.out.println("�ֹ濡�� ������ ��: " + message);
                      //(���̺��ȣ///�޴��̸�$$����$$����@@�޴��̸�$$����$$����)
                      //���̺� ��ȣ�� �޴��� ������.
                      st = new StringTokenizer(message,"///");
                      String tableNumber = st.nextToken();
                      String time = st.nextToken();
                      String allMenu = st.nextToken();
                      
//                      System.out.println("�ֹ濡�� ���� allMenu: "+allMenu);
                      //�޴����� ������(@@)
                      st2= new StringTokenizer(allMenu,"@@");

                      while(st2.hasMoreTokens()) {
                         String temp = st2.nextToken();
                         //�޴��� �̸�$$����$$���� ���� ������.
                         st3 = new StringTokenizer(temp,"$$");
                         obm = new OrderBoardMenu(st3.nextToken(), st3.nextToken());
                         st3.nextToken();   //�����ε� �ʿ�����Ƿ� �기��.
                         
                         
   //                      menuName = st3.nextToken();
   //                      menuCnt = st3.nextToken();
   //                      menuPrice = st3.nextToken();
                         
   //                      System.out.println("���̺� ��ȣ: " + tableNumber);
   //                      System.out.println("�޴� �̸�: " + menuName);
   //                      System.out.println("�޴� ����: " + menuCnt);
   //                      System.out.println("�޴� ����: " + menuPrice);
                         
                         //�޴����� ����Ʈ�� �޴� ��ü(�̸�,����)�� ����� �ִ´�.
                         menuList.add(obm);
                      }
                      
                      //�������忡 �޴����� �߰�
                      ordertoBoard(tableNumber, time);
                      //�������� ����Ʈ�� tableViewOl����
                      orderBoardlv.setItems(tableViewOl);
                      orderBoardlv.refresh();
                      //���̺� �� �ʱ�ȭ
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
     
        //���������� HBox���� �������忡 �߰��ϴ� �޼���
      @SuppressWarnings("unchecked")
      private void ordertoBoard(String tableNum, String time) {
            try {
               //���� �� ���̺� �� fxml 
               Parent node = FXMLLoader.load(getClass().getResource("OrderMenu.fxml"));
               Label fxtableNum = (Label)node.lookup("#time");
               Button orderCom = (Button)node.lookup("#orderCom");
               kitchenTableview = (TableView<OrderBoardMenu>)node.lookup("#kitchenTableview");
               
               //�ֹ����� �����ִ� ���̺� ��ȣ
               fxtableNum.setText(time);
               //Ȯ�ι�ư �׼�
               orderCom.setOnAction(e -> orderComAction(e));
               ObservableList<OrderBoardMenu> menuToTable = FXCollections.observableArrayList(); 
               
               for(OrderBoardMenu m : menuList) {
                  menuToTable.add(m);   
               }
               //��ư���� ī��Ʈ�� �ش�.
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
      //���̺� �� ������ ���� ����
         private void orderTableSettingg(String tableNumber) {
             TableColumn<OrderBoardMenu, ?> a = kitchenTableview.getColumns().get(0);
              a.setCellValueFactory(new PropertyValueFactory<>("menuName"));
              a.setText("���̺� ��ȣ : " + tableNumber);
              
              TableColumn<OrderBoardMenu, ?> b = kitchenTableview.getColumns().get(1);
              b.setCellValueFactory(new PropertyValueFactory<>("menuCnt"));
              b.setText("");
         }
         
         //�Ϸ��ư ������ �� ���̺� ����
         private void orderComAction(ActionEvent event) {
            //����Ʈ���� hbox�� �θ���
            for(HBox h : tableViewOl) {
               //hbox�� node ���� �ϳ��� ��� ��ư�� �̸��� Ȯ��
               for(int i=0; i<h.getChildren().size(); i++) {
                  AnchorPane n = (AnchorPane)h.getChildren().get(i);
                  Button b = (Button)n.getChildren().get(2);
                  //������ �ش� node ����
                  if(event.getTarget().toString().indexOf(b.getId()) != -1) {
                     System.out.println("��ư" + b.getId());
                     h.getChildren().remove(i);
                     //HBox�� ��尡 �Ѱ��� ������ Hbox ����
                     if(h.getChildren().size() == 0) {
                        tableViewOl.remove(h);
                     }
                     return;
                  }
               }
            }
         }
}