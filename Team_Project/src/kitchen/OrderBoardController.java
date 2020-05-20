package kitchen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OrderBoardController implements Initializable {
	
	   //�������ῡ �ʿ��� ���
	   private Socket socket;
	   private InputStream is;
	   private OutputStream os;
	   private DataInputStream dis;
	   private DataOutputStream dos;
	   StringTokenizer st;
	   StringTokenizer st2;
	   
	   @FXML TableView<OrderBoardMenu> kitchenTableview;  //OrderMenu.fxml ���̺� ��
	   @FXML Label tableNum; //���̺� ��ȣ �� 
	   @FXML TableColumn<OrderBoardMenu, String> orderMenuList; //���̺� �޴� �÷�
	   @FXML TableColumn<OrderBoardMenu, Integer> orderMenuQuantity; //���̺� ���� �÷�
	   
	   private ObservableList<OrderBoardMenu> tableViewOl = FXCollections.observableArrayList();
	   
	   
	   
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("start ��");
		startClient();
		System.out.println("start ��");
		temp();
	}
	
	public OrderBoardController() {
		
	}
	
	  private void startClient() {
	      try {
	         socket = new Socket();
	         socket.connect(new InetSocketAddress("localhost", 8888));
	         
	         is = socket.getInputStream();
	         dis = new DataInputStream(is);
	         os = socket.getOutputStream();
	         dos = new DataOutputStream(os);
	         
	         dos.writeUTF("�ֹ�");
	         System.out.println("���Ἲ��!");
	         
	         
	         
	   }catch (Exception e) {
		   e.printStackTrace();
	   }
	      
	  }
	  
	  public void temp() {
		  Thread thread = new Thread(new Runnable() {
	 			@Override
	 			public void run() {
	 				while(true) {
	 					System.out.println("�ֹ� ������");
	 					try {
	 						String message = dis.readUTF();
	 						System.out.println("�ֹ濡�� ������ ��: " + message);
	 						st = new StringTokenizer(message,"///");
	 						String kitchen = st.nextToken();
	 						String menu = st.nextToken();
	 						if(kitchen.equals("�ֹ�")) {
	 							System.out.println("�ֹ濡�� ���� �޴�"+menu);
	 							st2 = new StringTokenizer(menu,"$$");
	 							String tableNum = st2.nextToken();
	 							String menuName = st2.nextToken();
	 							String menuCnt = st2.nextToken();
	 							String menuPrice = st2.nextToken();
	 							
	 							System.out.println("���̺� ��ȣ: " + tableNum);
	 							System.out.println("�޴� �̸�: " + menuName);
	 							System.out.println("�޴� ����: " + menuCnt);
	 							System.out.println("�޴� ����: " + menuPrice);
	 							
	 							//�������忡 �޴����� �߰�
	 							
	 						}
	 					} catch (IOException e) {
	 						System.exit(0);
	 						e.printStackTrace();
	 					}	
	 				}
	 				
	 			}
	 		});
		  thread.start();
	  }
}