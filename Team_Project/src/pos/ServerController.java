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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import kitchen.OrderBoardMenu;
import pos.menu.Menu;
import pos.menu.MenuDAO;

public class ServerController implements Initializable{
	
	Stage serverStage = ServerMain.serverStage;
	Client kitchen; //�ֹ� Ŭ���̾�Ʈ
	
	//POS FXML ���
	private @FXML TabPane tab;
	private @FXML BorderPane borderPane;
	private @FXML GridPane home;	//���̺��� �ִ� ȭ��
	
	//table.fxml ���
	private TableView<OrderMenu> tableView;
	private Label labelPrice;
	
	private Parent addMenu;			//�޴� ���� ȭ��
	private Parent tablePayment; //���̺� ����ȭ��
	private Parent receipt; //������ ȭ��
	private Parent salesStatus; //�Ǹ���Ȳ ȭ��
	
	private MenuDAO dao;
	
//	private InetAddress ip;				//IP
	private ExecutorService threadPool;	//������Ǯ
	private ServerSocket serverSocket;	//��������
	private Socket socket;
	private List<Client> client_list = new ArrayList<>();
	private List<Parent> table_list = new ArrayList<>();
	
	StringTokenizer st;
	StringTokenizer st2;
	
	@Override
	public void initialize(URL location, ResourceBundle resources){
		try {
			addMenu = FXMLLoader.load(getClass().getResource("menu/menu.fxml"));
			tablePayment = FXMLLoader.load(getClass().getResource("tablepayment/TablePayment.fxml"));
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
			}else if(tab.equals("TablePayment")) {
				borderPane.setCenter(tablePayment);
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
		emptyTableInfo(99999);
	}
	
	private void startServer() {
		dao = MenuDAO.getinstance();
		try {
//			ip = InetAddress.getLocalHost();	//���� ��ǻ�� ������ �޾ƿ���
			threadPool = Executors.newFixedThreadPool(10);
			if(serverSocket == null) {
				serverSocket = new ServerSocket();
//				serverSocket.bind(new InetSocketAddress(ip.getHostAddress(), 5555));
				serverSocket.bind(new InetSocketAddress("localhost", 8888));
				System.out.println("���� ����");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
							e.printStackTrace();
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
			Platform.exit();
			System.exit(0);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void emptyTableInfo(int tableNo) {
		if(tableNo == 99999) {
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
		else {
			
		}
	}
	
	class Client extends Thread{
		
		private int tableNo;			//���̺� ��ȣ
		private List<Menu> menu_list = new ArrayList<>();	//�� ���̺��� ���� �޴� ����Ʈ ������ �����ȴ�.
//		private List<OrderMenu> orderMenu_list = new ArrayList<>();
		//�ֹ��� �޴� ����ִ� ����Ʈ
		private ObservableList<OrderMenu> orderMenu_list = FXCollections.observableArrayList();
		private Socket socket;			//�������� �����ϴ� �� ���̺��� ����
		
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		
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
			System.out.println("tmp: "+tmp);
			if(tmp.equals("�ֹ�")) {
				System.out.println("�ֹ� �´�");
				kitchen = this;
				return;
			}
			this.start();
			if(tmp != null) {
				tableNo = Integer.parseInt(tmp);
				//�̹� �������ִ� ���̺����� Ȯ��.(��ȣ �ߺ��ȵǰ�)
				if(client_list.size() == 0) {
					dos.writeUTF("connOk/");
				}else {
					for(Client c : client_list) {
						if(c.tableNo == tableNo) {
							dos.writeUTF("connFail/");
							return;
						}else {
							dos.writeUTF("connOk/");
						}
					}
				}
				System.out.println(tableNo + "����");
			}
			//���̺� ����
			makeTableInfo(tableNo);
			//���ӵǸ� �޴� ������ ����
			String menu = "";
			for(Menu m : dao.selectAll()) {
				//$$�� �޴���ȣ/ī�װ�/�̸�/���� �÷� ������ , @@�� �� ����
				menu += m.getMenuNum()+"$$"+ m.getCategory() + "$$" + m.getName() + "$$" + m.getPrice();
				menu += "@@";
				//�������� �����ϴ� ���̺��� �޴�����Ʈ���� �־��ش�.
				menu_list.add(m);
			}
			System.out.println(menu);
			menu = menu.substring(0, menu.length()-2);
//			�Ľ�Ÿ$$�˸��� �ø���$$15000��@@
//			���̺� �޴� ����
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
							e.printStackTrace();
							stopServer();
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
			//�ֹ� ����
			String message = st.nextToken();
			System.out.println("�������� : " + protocol);
			System.out.println("�޼��� : " + message);
			if(protocol.equals("�ֹ�")) {
				//
				st2 = new StringTokenizer(message, "@@");
				while(st2.hasMoreTokens()) {
					
					String menu = st2.nextToken();
					//�ֹ����� �޴� ����
					sendOrderInfo(menu);
					
					st = new StringTokenizer(menu, "$$");
					String name = st.nextToken();
					int cnt = Integer.parseInt(st.nextToken());
					int price = Integer.parseInt(st.nextToken());
					//ù �ֹ��� �ƴ� ��
					if(orderMenu_list.size() != 0) {
						for(OrderMenu m : orderMenu_list) {
							if(m.getName().equals(name)) {
								m.setCnt(m.getCnt() + cnt);
								m.setPrice(m.getPrice() + price);
								tableView.setItems(orderMenu_list);
								tableView.refresh();
								return;
							}
						}
					}
					orderMenu_list.add(new OrderMenu(name, cnt, price));
					tableView.setItems(orderMenu_list);
					tableView.refresh();
				}
				
			}
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
				
				//Į���� �ο� ���缭 ���̺����� �ֱ�
				Platform.runLater(() -> {
				home.getChildren().remove(tableNo-1);
				home.add(table, column, row);
				});
				table_list.add(table);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//���̺� ��ȣ, �ֹ���������
		private void sendOrderInfo(String menu) {
			//�ֹ����� �޴� ����
			try {
				kitchen.dos.writeUTF("�ֹ�///"+tableNo+"$$"+menu);
				System.out.println("�ֹ����� ������ �޴�: �ֹ�///" + menu);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}