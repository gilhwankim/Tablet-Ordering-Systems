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
	   private @FXML TableColumn<OrderBoardMenu, String> orderMenuList; //���̺� �޴� �÷�
	   private @FXML TableColumn<OrderBoardMenu, String> orderMenuQuantity; //���̺� ���� �÷�
	   private @FXML ListView<HBox> orderBoardlv; //����Ʈ��
	   private ObservableList<HBox> tableViewOl = FXCollections.observableArrayList();
	   List<OrderBoardMenu> menuList = new ArrayList<>();
		String tableNumber;
		String menuName;
		String menuCnt ;
		String menuPrice ;
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
	 						String tableNumber = st.nextToken();
	 						String allMenu = st.nextToken();
	 						
 							System.out.println("�ֹ濡�� ���� allMenu: "+allMenu);
 							st2= new StringTokenizer(allMenu,"@@");

 							while(st2.hasMoreTokens()) {
 							String temp = st2.nextToken();
 							System.out.println("st2");
 							System.out.println();
 							st3 = new StringTokenizer(temp,"$$");
 							menuName = st3.nextToken();
 							menuCnt = st3.nextToken();
 							menuPrice = st3.nextToken();
 							
 							System.out.println("���̺� ��ȣ: " + tableNumber);
 							System.out.println("�޴� �̸�: " + menuName);
 							System.out.println("�޴� ����: " + menuCnt);
 							System.out.println("�޴� ����: " + menuPrice);
 							
 							//�޴����� ����Ʈ
 							menuList.add(new OrderBoardMenu(menuName,menuCnt));
 							}
 							
 							//������� �ȴ�@@@@@@@@@@@@@@@@@@
 							
 							System.out.println("�޴�����Ʈ ������: "+menuList.size());
 							
 							//�������忡 �޴����� �߰�
 							tableViewOl = ordertoBoard(tableViewOl,tableNumber,menuList);
 							orderBoardlv.setItems(tableViewOl);
 							System.out.println("���ΰ�ħ");
 							orderBoardlv.refresh();
 							//���̺� �� �ʱ�ȭ
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
	  	//���������� HBox���� �������忡 �߰��ϴ� �޼���
		private ObservableList<HBox> ordertoBoard(ObservableList<HBox> ol, String tableNum,List<OrderBoardMenu> list) {
				ObservableList<HBox> tempOl = ol;
				
				try {
					//���� �� ���̺� �� fxml 
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
		//���̺� �� ������ ���� ����
	      private void orderTableSettingg() {
	          TableColumn<OrderBoardMenu, ?> a = kitchenTableview.getColumns().get(0);
	           a.setCellValueFactory(new PropertyValueFactory<>("menuName"));
	           a.setText("�޴�");
	           
	           TableColumn<OrderBoardMenu, ?> b = kitchenTableview.getColumns().get(1);
	           b.setCellValueFactory(new PropertyValueFactory<>("menuCnt"));
	           b.setText("����");
	       }
}