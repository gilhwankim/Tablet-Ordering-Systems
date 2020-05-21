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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import tablet.OrderMenu;

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
	   private @FXML TableColumn<OrderBoardMenu, String> orderMenuList; //테이블 메뉴 컬럼
	   private @FXML TableColumn<OrderBoardMenu, String> orderMenuQuantity; //테이블 수량 컬럼
	   private @FXML ListView<HBox> orderBoardlv; //리스트뷰
	   private ObservableList<HBox> tableViewOl = FXCollections.observableArrayList();
	   List<OrderBoardMenu> menuList = new ArrayList<>();
		String tableNumber;
		String menuName;
		String menuCnt ;
		String menuPrice ;
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
	 						String tableNumber = st.nextToken();
	 						String allMenu = st.nextToken();
	 						
 							System.out.println("주방에서 받은 allMenu: "+allMenu);
 							st2= new StringTokenizer(allMenu,"@@");

 							while(st2.hasMoreTokens()) {
 							String temp = st2.nextToken();
 							System.out.println("st2");
 							System.out.println();
 							st3 = new StringTokenizer(temp,"$$");
 							menuName = st3.nextToken();
 							menuCnt = st3.nextToken();
 							menuPrice = st3.nextToken();
 							
 							System.out.println("테이블 번호: " + tableNumber);
 							System.out.println("메뉴 이름: " + menuName);
 							System.out.println("메뉴 수량: " + menuCnt);
 							System.out.println("메뉴 가격: " + menuPrice);
 							
 							//메뉴담을 리스트
 							menuList.add(new OrderBoardMenu(menuName,menuCnt));
 							}
 							
 							//여기까지 된다@@@@@@@@@@@@@@@@@@
 							
 							System.out.println("메뉴리스트 사이즈: "+menuList.size());
 							
 							//오더보드에 메뉴내역 추가
 							tableViewOl = ordertoBoard(tableViewOl,tableNumber,menuList);
 							orderBoardlv.setItems(tableViewOl);
 							System.out.println("새로고침");
 							orderBoardlv.refresh();
 							//테이블 뷰 초기화
 							orderTableSettingg();
 							menuList.clear();
 							
	 					} catch (IOException e) {
	 						System.exit(0);
	 						e.printStackTrace();
	 					}	
	 				}
	 				
	 			}
	 		});
		  thread.start();
	  }
	  	//오더들어오면 HBox만들어서 오더보드에 추가하는 메서드
		private ObservableList<HBox> ordertoBoard(ObservableList<HBox> ol, String tableNum,List<OrderBoardMenu> list) {
				ObservableList<HBox> tempOl = ol;
				
				try {
					//오더 들어갈 테이블 뷰 fxml 
					Parent node = FXMLLoader.load(getClass().getResource("OrderMenu.fxml"));
					Label fxtableNum = (Label)node.lookup("#tableNum");
					kitchenTableview = (TableView<OrderBoardMenu>)node.lookup("#kitchenTableview");
					fxtableNum.setText(tableNum);
					ObservableList<OrderBoardMenu> menuToTable = FXCollections.observableArrayList(); 
					
					for(OrderBoardMenu m : list) {
						menuToTable.add(m);	
					}
					
					kitchenTableview.setItems(menuToTable);
					if(tempOl.size() == 0) {
			               HBox hbox = new HBox();
			               hbox.setSpacing(10);
			               hbox.getChildren().add(node);
			               tempOl.add(hbox);
			            }else if(tempOl.get(tempOl.size()-1).getChildren().size() % 4 == 0 ) {
			               HBox hbox = new HBox();
			               hbox.setSpacing(10);
			               hbox.getChildren().add(node);
			               tempOl.add(hbox);
			            }else {
			            	Platform.runLater(()->tempOl.get(tempOl.size()-1).getChildren().add(node));
			            }
			         }catch (Exception e) {
			        	 e.printStackTrace();
			         }
			         return tempOl;
		}
		//테이블 뷰 데이터 형식 지정
	      private void orderTableSettingg() {
	          TableColumn<OrderBoardMenu, ?> a = kitchenTableview.getColumns().get(0);
	           a.setCellValueFactory(new PropertyValueFactory<>("menuName"));
	           a.setText("메뉴");
	           
	           TableColumn<OrderBoardMenu, ?> b = kitchenTableview.getColumns().get(1);
	           b.setCellValueFactory(new PropertyValueFactory<>("menuCnt"));
	           b.setText("수량");
	       }
}