package pos.tablepayment;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import pos.menu.Menu;

public class MakeTab {

	private Tab tab;
	private FlowPane fp;
	private StackPane sp;
	
	private boolean flag = false;
	
	public MakeTab() {}
	 
	public TabPane make(List<Menu> list, TabPane tabPane) {
		TabPane tp = tabPane;
		try {
		for(Menu m : list) {
			//탭이 한개도 없을 떄
			if(tp.getTabs().size() == 0) {
				 //탭을 새로만든다.(메뉴의 종류 ex)샐러드,파스타..)
				 tab = new Tab(m.getCategory());
				 //탭 안에 들어갈 플로우페인
				 fp = new FlowPane(10D, 5D);
				 //플로우페인 안에 들어갈 버튼
				 sp = node(m.getName(), m.getPrice());
				 fp.getChildren().add(sp);
				 tab.setContent(fp);
				 tp.getTabs().add(tab);
			}else {
				//탭이 하나이상일 때 탭의 제목과 메뉴의 카테고리를 비교
				for(Tab t : tp.getTabs()) {
					if(t.getText().equals(m.getCategory())) {
						fp = (FlowPane)t.getContent();
						sp = node(m.getName(), m.getPrice());
						fp.getChildren().add(sp);
						//있으면 넣고 flag = true
						flag = true;
						break;
					}
				}
				//만약 지금 메뉴가 들어가서 flag가 true이면 실행하지않는 부분
				//메뉴의 카테고리와 탭의 제목이 안맞아서 못들어간 메뉴가 있으면
				//새로운 탭 생성
				if(flag == false) {
					tab = new Tab(m.getCategory());
					fp = new FlowPane(10D, 5D);
					sp = node(m.getName(), m.getPrice());
					fp.getChildren().add(sp);
					tab.setContent(fp);
					tp.getTabs().add(tab);
				}
				flag = false;
			}
		}
		return tp;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return tp;
	}
	
	//Flowpane안에 들어갈 버튼들 각 메뉴의 이름과 가격을 가지고 생성한다.
	private StackPane node(String name, String price) {
		StackPane node = null;
		try {
			node = FXMLLoader.load(getClass().getResource("TablePaymentMenuBtn.fxml"));
			Button PaymentMenuBtn = (Button)node.lookup("#PaymentMenuBtn");
			PaymentMenuBtn.setText(name);
			Label PaymentMenuPrice = (Label)node.lookup("#PaymentMenuPrice");
			PaymentMenuPrice.setText(price);
			return node;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}
	 
}
