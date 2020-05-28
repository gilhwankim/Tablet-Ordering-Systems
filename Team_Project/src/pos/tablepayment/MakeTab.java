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
			//���� �Ѱ��� ���� ��
			if(tp.getTabs().size() == 0) {
				 //���� ���θ����.(�޴��� ���� ex)������,�Ľ�Ÿ..)
				 tab = new Tab(m.getCategory());
				 //�� �ȿ� �� �÷ο�����
				 fp = new FlowPane(10D, 5D);
				 //�÷ο����� �ȿ� �� ��ư
				 sp = node(m.getName(), m.getPrice());
				 fp.getChildren().add(sp);
				 tab.setContent(fp);
				 tp.getTabs().add(tab);
			}else {
				//���� �ϳ��̻��� �� ���� ����� �޴��� ī�װ��� ��
				for(Tab t : tp.getTabs()) {
					if(t.getText().equals(m.getCategory())) {
						fp = (FlowPane)t.getContent();
						sp = node(m.getName(), m.getPrice());
						fp.getChildren().add(sp);
						//������ �ְ� flag = true
						flag = true;
						break;
					}
				}
				//���� ���� �޴��� ���� flag�� true�̸� ���������ʴ� �κ�
				//�޴��� ī�װ��� ���� ������ �ȸ¾Ƽ� ���� �޴��� ������
				//���ο� �� ����
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
	
	//Flowpane�ȿ� �� ��ư�� �� �޴��� �̸��� ������ ������ �����Ѵ�.
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
