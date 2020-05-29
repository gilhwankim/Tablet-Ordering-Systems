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
	   private List<AnchorPane> nodeList = new ArrayList<>();
	   private List<OrderBoardMenu> menuList = new ArrayList<>();
	   private OrderBoardMenu obm;
	   private int cnt = 0;
	   
	   @FXML Label dateLabel; //�������� ��� ��¥
	   @FXML Label timeLabel;// �������� ��� �ð�
	   
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		startClient();
		//�������� ��� ��¥
		new Clock(dateLabel,timeLabel);
		 //�����ư
		KitchenMain.KitchenStage.setOnCloseRequest( e -> stopClient());
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
	           dos.writeUTF("����///�ֹ�");
	           dos.flush();
	           if(!socket.isClosed()) {
	                socket.close();
	            }
	           is.close();
	            dis.close();
	            os.close();
	            dos.close();
	            
	            System.out.println("�ֹ� ����.");
	            System.exit(0);
	             }catch (Exception e) {
	                System.exit(0);
	             }
	     }
	  
	  //������ �����ϸ�, �����κ��� ���̺���� �ֹ��� ���޹޴´�.
	  public void kitchenConnect() {
		  Thread thread = new Thread(new Runnable() {
	 			@Override
	 			public void run() {
	 				while(true) {
	 					try {
	 						String message = dis.readUTF();
	 						
	 						//(���̺��ȣ///�޴��̸�$$����$$����@@�޴��̸�$$����$$����)
	 						//���̺� ��ȣ�� �޴��� ������.
	 						st = new StringTokenizer(message,"///");
	 						String tableNumber = st.nextToken();
	 						String time = st.nextToken();
	 						String allMenu = st.nextToken();
	 						
	 						//�޴����� ������(@@)
 							st2= new StringTokenizer(allMenu,"@@");

 							while(st2.hasMoreTokens()) {
	 							String temp = st2.nextToken();
	 							//�޴��� �̸�$$����$$���� ���� ������.
	 							st3 = new StringTokenizer(temp,"$$");
	 							obm = new OrderBoardMenu(st3.nextToken(), st3.nextToken());
	 							st3.nextToken();	//�����ε� �ʿ�����Ƿ� �기��.
	 							
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
					//���� �� ���̺� �� fxml 
					AnchorPane node = FXMLLoader.load(getClass().getResource("OrderMenu.fxml"));
					Label fxtableNum = (Label)node.lookup("#time");
					Button orderCom = (Button)node.lookup("#orderCom");
					kitchenTableview = (TableView<OrderBoardMenu>)node.lookup("#kitchenTableview");
					//�ֹ� ������ �˸���
					MakeSound.kitchenOrderSound();
					
					//�ֹ����� �����ִ� ���̺� ��ȣ
					fxtableNum.setText(time);
					//Ȯ�ι�ư �׼�
					
					ObservableList<OrderBoardMenu> menuToTable = FXCollections.observableArrayList(); 
					
					for(OrderBoardMenu m : menuList) {
						menuToTable.add(m);
					}
					//��ư���� ī��Ʈ�� �ش�.
					orderCom.setId(orderCom.getId() + cnt++);
					
					kitchenTableview.setItems(menuToTable);
					nodeList.add(node);
					
					Platform.runLater(()->addNode(node));
					
					orderCom.setOnAction(e -> orderComAction(e));
					
			         }catch (Exception e) {
			        	 e.printStackTrace();
			         }
		}
		//���̺� �� ������ ���� ����
	      private void orderTableSettingg(String tableNumber) {
	          TableColumn<OrderBoardMenu, ?> a = kitchenTableview.getColumns().get(0);
	           a.setCellValueFactory(new PropertyValueFactory<>("menuName"));
	           a.setText("���̺� ��ȣ : ");
	           
	           TableColumn<OrderBoardMenu, ?> b = kitchenTableview.getColumns().get(1);
	           b.setCellValueFactory(new PropertyValueFactory<>("menuCnt"));
	           b.setText(tableNumber);
	      }
	      
	      //�Ϸ��ư ������ �� ���̺� ����
	      private void orderComAction(ActionEvent event) {
	    	  //����Ʈ���� hbox�� �θ���
	    	  for(HBox hbox : tableViewOl) {
	    		  //hbox�� node ���� �ϳ��� ��� ��ư�� �̸��� Ȯ��
	    		  for(int i=0; i<hbox.getChildren().size(); i++) {
	    			  AnchorPane ap = (AnchorPane)hbox.getChildren().get(i);
	    			  Button button = (Button)ap.getChildren().get(2);
	    			  //������ �ش� node ����
	    			  if(event.getTarget().toString().indexOf(button.getId()) != -1) {
	    				  //���õ� ���(ap)�� ����Ʈ���� �����.
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
	      //fxml�� ���� ��带 ����Ʈ�信 ������ HBox�ȿ� �ִ´�. 
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