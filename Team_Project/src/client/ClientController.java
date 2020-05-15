package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import server.menu.Menu;

public class ClientController implements Initializable{
	
	@FXML Button btn;
	@FXML ChoiceBox<String> cb;
	
	private ObservableList<String> ol = FXCollections.observableArrayList();
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private DataInputStream dis;
	private DataOutputStream dos;
	
	private List<Menu> menuList = new ArrayList<>();
	private StringTokenizer st1;
	private StringTokenizer st2;
	
	private Stage tableStage = ClientMain.clientStage;
	
	//table.fxml
	private Button plusBtn;
	private ListView<HBox> lvSalad;
	private ObservableList<HBox> saladOl = FXCollections.observableArrayList();
	private ListView<HBox> lvPasta;
	private ObservableList<HBox> PastaOl = FXCollections.observableArrayList();
	
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ol.addAll("1","2","3","4","5","6","7","8","9","10","11","12","13","14","15");
		cb.setItems(ol);
		
		
		btn.setOnAction( e -> {
			String tableNo = cb.getSelectionModel().getSelectedItem();
			if(tableNo != null) {
				startClient(tableNo);
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void startClient(String tableNo) {
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress("localhost", 8888));
			
			is = socket.getInputStream();
			dis = new DataInputStream(is);
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			//접속하고 테이블 번호를 서버에 전송
			dos.writeUTF(tableNo);
			
			//내이름과 같은 테이블이 있는지 서버에 체크 (성공하면 connOk, 실패시 connFail)
			String message = dis.readUTF();
			//false가 나오면 리턴시켜 다시 자리정하게한다.
			if(!connCheck(message)) {
				return;
			}
			
			//메뉴를 서버로부터 받는다.
			String menu = dis.readUTF();
			//메뉴를 받아오는데에 성공했을 때
			if(menu != null) {
				st1 = new StringTokenizer(menu, "@@");
				while(st1.hasMoreTokens()) {
					String tmp = st1.nextToken();
					st2 = new StringTokenizer(tmp, "$$");
					menuList.add(new Menu(st2.nextToken(), st2.nextToken(), st2.nextToken()));
				}
				//자리 정하는게 끝나면 메뉴창을 연다.
				Parent parent = FXMLLoader.load(getClass().getResource("table.fxml"));
				Scene scene = new Scene(parent);
				this.tableStage.setScene(scene);
				lvSalad = (ListView<HBox>)parent.lookup("#lvSalad");
				lvPasta = (ListView<HBox>)parent.lookup("#lvPasta");
				//메뉴들을 각 메뉴판에 담기
				for(Menu m : menuList) {
					if(m.getNo().equals("파스타")) {
						PastaOl = replaceMenu(PastaOl, m);
					}else if(m.getNo().equals("샐러드")) {
						saladOl = replaceMenu(saladOl, m);
					}
					//리스트뷰에 observablelist 연동
					lvSalad.setItems(saladOl);
					lvPasta.setItems(PastaOl);
				}
				
			}else {
				throw new Exception(); 
			}
		}catch (Exception e) {
			System.out.println("메뉴 정보를 받아오지 못함.");
			e.printStackTrace();
		}
	}
	
	//메뉴판에 메뉴를 넣는 메서드
	private ObservableList<HBox> replaceMenu(ObservableList<HBox> ol, Menu m){
		ObservableList<HBox> tempOl = ol;
		try {
			Parent node = FXMLLoader.load(getClass().getResource("GridRow.fxml"));
			Label labelName = (Label)node.lookup("#labelName");
			Label labelPrice = (Label)node.lookup("#labelPrice");
			labelName.setText(m.getName());
			labelPrice.setText(m.getPrice());
			if(tempOl.size() == 0) {
				HBox hbox = new HBox();
				hbox.setSpacing(10);
				hbox.getChildren().add(node);
				tempOl.add(hbox);
			}else if(tempOl.get(tempOl.size()-1).getChildren().size() % 3 == 0 ) {
				HBox hbox = new HBox();
				hbox.setSpacing(10);
				hbox.getChildren().add(node);
				tempOl.add(hbox);
			}else {
				tempOl.get(tempOl.size()-1).getChildren().add(node);
			}
			node.setOnMouseClicked(e -> {
				if(e.getClickCount() == 2) {
				    System.out.println("메뉴이름 : " + labelName.getText() + "메뉴가격 : " + labelPrice.getText());			
				}
			});
		}catch (Exception e) {
		}
		return tempOl;
	}
	
	//접속 체크
	private boolean connCheck(String message) {
		StringTokenizer st = new StringTokenizer(message, "/");
		String protocol = st.nextToken();
//		String msg = st.nextToken();
		System.out.println("프로토콜" + protocol);
		if(protocol.equals("connOk")) {
			return true;
		}else if(protocol.equals("connFail")) {
			return false;
		}
		return false;
	}
}
