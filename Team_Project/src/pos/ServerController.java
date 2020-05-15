package pos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pos.menu.Menu;
import pos.menu.MenuDAO;

public class ServerController implements Initializable{
	
	Stage serverStage = ServerMain.serverStage;
	
	//FXML 멤버
	private @FXML TabPane tab;
	private @FXML BorderPane borderPane;
	private @FXML GridPane home;	//테이블이 있는 화면
	
	private Parent addMenu;			//메뉴 관리 화면
	private MenuDAO dao;
	
//	private InetAddress ip;				//IP
	private ExecutorService threadPool;	//스레드풀
	private ServerSocket serverSocket;	//서버소켓
	private Socket socket;
	private List<Client> client_list = new ArrayList<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources){
		try {
			addMenu = FXMLLoader.load(getClass().getResource("menu/menu.fxml"));
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
			}
		});
		
		//서버 시작
		startServer();
		serverStage.setOnCloseRequest(e -> stopServer());
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
//							client.start();
							
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
			if(serverSocket != null && serverSocket.isClosed()) {
				serverSocket.close();
			}
			if(threadPool != null && threadPool.isShutdown()) {
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
	
	class Client extends Thread{
		
		private int tableNo;			//테이블 번호
		private List<Menu> menu_list = new ArrayList<>();	//각 테이블이 가질 메뉴 리스트 서버와 연동된다.
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
			//접속되면 메뉴 정보를 전송
			String menu = "";
			for(Menu m : dao.selectAll()) {
				menu += m.getNo() + "$$" + m.getName() + "$$" + m.getPrice();
				menu += "@@";
				//보내주고 서버에서 관리하는 테이블의 메뉴리스트에도 넣어준다.
				menu_list.add(m);
			}
			
			menu = menu.substring(0, menu.length()-2);
			System.out.println("메뉴 : " + menu);
//			파스타$$알리오 올리오$$15000원@@
			if(menu != null) {
				dos.writeUTF(menu);
			}
			client_list.add(this);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		}
	}
}
