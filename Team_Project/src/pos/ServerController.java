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
	Client kitchen; //주방 클라이언트
	
	//POS FXML 멤버
	private @FXML TabPane tab;
	private @FXML BorderPane borderPane;
	private @FXML GridPane home;	//테이블이 있는 화면
	
	//table.fxml 멤버
	private TableView<OrderMenu> tableView;
	private Label labelPrice;
	
	private Parent addMenu;			//메뉴 관리 화면
	private Parent tablePayment; //테이블 결제화면
	private Parent receipt; //영수증 화면
	private Parent salesStatus; //판매현황 화면
	
	private MenuDAO dao;
	
//	private InetAddress ip;				//IP
	private ExecutorService threadPool;	//스레드풀
	private ServerSocket serverSocket;	//서버소켓
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
		
		//오른쪽 탭 리스너
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
		
		//서버 시작
		startServer();
		serverStage.setOnCloseRequest(e -> stopServer());
		
		//처음에 테이블 초기화
		emptyTableInfo(99999);
	}
	
	private void startServer() {
		dao = MenuDAO.getinstance();
		try {
//			ip = InetAddress.getLocalHost();	//현재 컴퓨터 아이피 받아오기
			threadPool = Executors.newFixedThreadPool(10);
			if(serverSocket == null) {
				serverSocket = new ServerSocket();
//				serverSocket.bind(new InetSocketAddress(ip.getHostAddress(), 5555));
				serverSocket.bind(new InetSocketAddress("localhost", 8888));
				System.out.println("서버 시작");
			}
		} catch (Exception e) {
			e.printStackTrace();
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
					Label emptyLabel = new Label(1+j+home.getColumnConstraints().size()*i + " 번 테이블 대기");
					empty.getChildren().add(emptyLabel);
					home.add(empty, j, i);
				}
			}
		}
		else {
			
		}
	}
	
	class Client extends Thread{
		
		private int tableNo;			//테이블 번호
		private List<Menu> menu_list = new ArrayList<>();	//각 테이블이 가질 메뉴 리스트 서버와 연동된다.
//		private List<OrderMenu> orderMenu_list = new ArrayList<>();
		//주문한 메뉴 담고있는 리스트
		private ObservableList<OrderMenu> orderMenu_list = FXCollections.observableArrayList();
		private Socket socket;			//서버에서 관리하는 각 테이블의 소켓
		
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
			
			//처음 들어올 때 테이블 넘버를 받는다.
			String tmp = dis.readUTF();
			//주방 연결 확인
			System.out.println("tmp: "+tmp);
			if(tmp.equals("주방")) {
				System.out.println("주방 맞다");
				kitchen = this;
				return;
			}
			this.start();
			if(tmp != null) {
				tableNo = Integer.parseInt(tmp);
				//이미 접속해있는 테이블인지 확인.(번호 중복안되게)
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
				System.out.println(tableNo + "접속");
			}
			//테이블 연결
			makeTableInfo(tableNo);
			//접속되면 메뉴 정보를 전송
			String menu = "";
			for(Menu m : dao.selectAll()) {
				//$$는 메뉴번호/카테고리/이름/가격 컬럼 구분자 , @@는 행 구분
				menu += m.getMenuNum()+"$$"+ m.getCategory() + "$$" + m.getName() + "$$" + m.getPrice();
				menu += "@@";
				//서버에서 관리하는 테이블의 메뉴리스트에도 넣어준다.
				menu_list.add(m);
			}
			System.out.println(menu);
			menu = menu.substring(0, menu.length()-2);
//			파스타$$알리오 올리오$$15000원@@
//			테이블에 메뉴 전송
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
							e.printStackTrace();
							stopServer();
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
			//주문 내역
			String message = st.nextToken();
			System.out.println("프로토콜 : " + protocol);
			System.out.println("메세지 : " + message);
			if(protocol.equals("주문")) {
				//
				st2 = new StringTokenizer(message, "@@");
				while(st2.hasMoreTokens()) {
					
					String menu = st2.nextToken();
					//주방으로 메뉴 전송
					sendOrderInfo(menu);
					
					st = new StringTokenizer(menu, "$$");
					String name = st.nextToken();
					int cnt = Integer.parseInt(st.nextToken());
					int price = Integer.parseInt(st.nextToken());
					//첫 주문이 아닐 때
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
				
				//칼럼과 로우 맞춰서 테이블정보 넣기
				Platform.runLater(() -> {
				home.getChildren().remove(tableNo-1);
				home.add(table, column, row);
				});
				table_list.add(table);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//테이블 번호, 주문내역전송
		private void sendOrderInfo(String menu) {
			//주방으로 메뉴 보냄
			try {
				kitchen.dos.writeUTF("주방///"+tableNo+"$$"+menu);
				System.out.println("주방으로 보내는 메뉴: 주방///" + menu);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}