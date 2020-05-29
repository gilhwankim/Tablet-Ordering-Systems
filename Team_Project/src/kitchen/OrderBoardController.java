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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import makeSound.MakeSound;

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
	   private List<AnchorPane> nodeList = new ArrayList<>();
	   private List<OrderBoardMenu> menuList = new ArrayList<>();
	   private OrderBoardMenu obm;
	   private int cnt = 0;
	   
	   @FXML Label dateLabel; //오더보드 상단 날짜
	   @FXML Label timeLabel;// 오더보드 상단 시간
	   
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		startClient();
		//오더보드 상단 날짜
		new Clock(dateLabel,timeLabel);
		 //종료버튼
		KitchenMain.KitchenStage.setOnCloseRequest( e -> stopClient());
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
	         kitchenConnect();
	         
	   }catch (Exception e) {
		   if(!socket.isClosed()) {
	            try {
	                is.close();
	                dis.close();
	                os.close();
	                dos.close();
	                socket.close();
	             }catch (Exception e2) {
	                e2.printStackTrace();
	                System.exit(0);
	             }
	          }else {
	             System.exit(0);
	          }
	   		}
	  	}
	  
	  private void stopClient() {
	        try {
	           dos.writeUTF("종료///주방");
	           dos.flush();
	           if(!socket.isClosed()) {
	                socket.close();
	            }
	           is.close();
	            dis.close();
	            os.close();
	            dos.close();
	            
	            System.out.println("주방 종료.");
	            System.exit(0);
	             }catch (Exception e) {
	                System.exit(0);
	             }
	     }
	  
	  //연결이 성공하면, 서버로부터 테이블들의 주문을 전달받는다.
	  public void kitchenConnect() {
		  Thread thread = new Thread(new Runnable() {
	 			@Override
	 			public void run() {
	 				while(true) {
	 					try {
	 						String message = dis.readUTF();
	 						
	 						//(테이블번호///메뉴이름$$수량$$가격@@메뉴이름$$수량$$가격)
	 						//테이블 번호와 메뉴를 나눈다.
	 						st = new StringTokenizer(message,"///");
	 						String tableNumber = st.nextToken();
	 						String time = st.nextToken();
	 						String allMenu = st.nextToken();
	 						
	 						//메뉴별로 나눈다(@@)
 							st2= new StringTokenizer(allMenu,"@@");

 							while(st2.hasMoreTokens()) {
	 							String temp = st2.nextToken();
	 							//메뉴의 이름$$수량$$가격 으로 나눈다.
	 							st3 = new StringTokenizer(temp,"$$");
	 							obm = new OrderBoardMenu(st3.nextToken(), st3.nextToken());
	 							st3.nextToken();	//가격인데 필요없으므로 흘린다.
	 							
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
	 						if(!socket.isClosed()) {
	 				            try {
	 				                is.close();
	 				                dis.close();
	 				                os.close();
	 				                dos.close();
	 				                socket.close();
	 				             }catch (Exception e2) {
	 				                e2.printStackTrace();
	 				                System.exit(0);
	 				             }
	 				          }else {
	 				             System.exit(0);
	 				          }
	 					}	
	 				}
	 				
	 			}
	 		});
		  thread.start();
	  }
	  
		@SuppressWarnings("unchecked")
		private void ordertoBoard(String tableNum, String time) {
				try {
					//오더 들어갈 테이블 뷰 fxml 
					AnchorPane node = FXMLLoader.load(getClass().getResource("OrderMenu.fxml"));
					Label fxtableNum = (Label)node.lookup("#time");
					Button orderCom = (Button)node.lookup("#orderCom");
					kitchenTableview = (TableView<OrderBoardMenu>)node.lookup("#kitchenTableview");
					//주문 들어오면 알림음
					MakeSound.kitchenOrderSound();
					
					//주문마다 적혀있는 테이블 번호
					fxtableNum.setText(time);
					//확인버튼 액션
					
					ObservableList<OrderBoardMenu> menuToTable = FXCollections.observableArrayList(); 
					
					for(OrderBoardMenu m : menuList) {
						menuToTable.add(m);
					}
					//버튼마다 카운트를 준다.
					orderCom.setId(orderCom.getId() + cnt++);
					
					kitchenTableview.setItems(menuToTable);
					nodeList.add(node);
					
					Platform.runLater(()->addNode(node));
					
					orderCom.setOnAction(e -> orderComAction(e));
					
			         }catch (Exception e) {
			        	 e.printStackTrace();
			         }
		}
		//테이블 뷰 데이터 형식 지정
	      private void orderTableSettingg(String tableNumber) {
	          TableColumn<OrderBoardMenu, ?> a = kitchenTableview.getColumns().get(0);
	           a.setCellValueFactory(new PropertyValueFactory<>("menuName"));
	           a.setText("테이블 번호 : ");
	           
	           TableColumn<OrderBoardMenu, ?> b = kitchenTableview.getColumns().get(1);
	           b.setCellValueFactory(new PropertyValueFactory<>("menuCnt"));
	           b.setText(tableNumber);
	      }
	      
	      //완료버튼 눌렀을 때 테이블 삭제
	      private void orderComAction(ActionEvent event) {
	    	  //리스트에서 hbox를 부른다
	    	  for(HBox hbox : tableViewOl) {
	    		  //hbox에 node 들을 하나씩 골라 버튼의 이름을 확인
	    		  for(int i=0; i<hbox.getChildren().size(); i++) {
	    			  AnchorPane ap = (AnchorPane)hbox.getChildren().get(i);
	    			  Button button = (Button)ap.getChildren().get(2);
	    			  //맞으면 해당 node 삭제
	    			  if(event.getTarget().toString().indexOf(button.getId()) != -1) {
	    				  //선택된 노드(ap)를 리스트에서 지운다.
	    				  nodeList.remove(ap);
	    				  tableViewOl.removeAll(tableViewOl);
	    				  for(AnchorPane tmpAp : nodeList) {
	    					  Platform.runLater(()->addNode(tmpAp)); 
	    				  }
	    				  return;
	    			  }
	    			  System.out.println();
	    		  }
	    	  }
	      }
	      //fxml을 담은 노드를 리스트뷰에 생성한 HBox안에 넣는다. 
	     private void addNode(AnchorPane node) {
	    	 if(tableViewOl.size() == 0) {
	               HBox hbox = new HBox();
	               hbox.setSpacing(10);
	               hbox.getChildren().add(node);
	               tableViewOl.add(hbox);
	            }else if(tableViewOl.get(tableViewOl.size()-1).getChildren().size() % 4 == 0 ) {
	               HBox hbox = new HBox();
	               hbox.setSpacing(10);
	               hbox.getChildren().add(node);
	               tableViewOl.add(hbox);
	            }else {
	            	tableViewOl.get(tableViewOl.size()-1).getChildren().add(node);
	            }
	     }
}