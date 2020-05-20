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
import pos.menu.Menu;

public class OrderBoardController implements Initializable {
	
	   //서버연결에 필요한 멤버
	   private Socket socket;
	   private InputStream is;
	   private OutputStream os;
	   private DataInputStream dis;
	   private DataOutputStream dos;
	   StringTokenizer st;
	   
	   @FXML TableView<OrderBoardMenu> kitchenTableview;  //OrderMenu.fxml 테이블 뷰
	   @FXML Label tableNum; //테이블 번호 라벨 
	   @FXML TableColumn<OrderBoardMenu, String> orderMenuList; //테이블 메뉴 컬럼
	   @FXML TableColumn<OrderBoardMenu, Integer> orderMenuQuantity; //테이블 수량 컬럼
	   
	   private ObservableList<OrderBoardMenu> tableViewOl = FXCollections.observableArrayList();
	   
	   
	   
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("start 앞");
		startClient();
		System.out.println("start 뒤");
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
	         
	         dos.writeUTF("주방");
	         System.out.println("연결성공!");
	         
	         
	         
	   }catch (Exception e) {
		   e.printStackTrace();
	   }
	      
	  }
	  
	  public void temp() {
		  Thread thread = new Thread(new Runnable() {
	 			@Override
	 			public void run() {
	 				while(true) {
	 					System.out.println("주방 쓰레드");
	 					try {
	 						String message = dis.readUTF();
	 						System.out.println("주방에서 받은거 다: " + message);
	 						st = new StringTokenizer(message,"///");
	 						String kitchen = st.nextToken();
	 						String menu = st.nextToken();
	 						if(kitchen.equals("주방")) {
	 							System.out.println("주방에서 받은 메뉴"+menu);
	 						}
	 					} catch (IOException e) {
	 						e.printStackTrace();
	 					}	
	 				}
	 				
	 			}
	 		});
		  thread.start();
	  }
}