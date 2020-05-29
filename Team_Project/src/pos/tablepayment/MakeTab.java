package pos.tablepayment;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import pos.OrderMenu;
import pos.ServerController.Client;
import pos.menu.Menu;

public class MakeTab {

   private Tab tab;
   private FlowPane fp;
   private StackPane sp;
   
   private boolean flag = false;
   
   private List<Menu> menuList;
   private Client client;
   
   public MakeTab() {}
    
   public TabPane make(List<Menu> list, TabPane tabPane) {
      TabPane tp = tabPane;
      menuList = list;
      try {
      for(Menu m : menuList) {
         //���� �Ѱ��� ���� ��
         if(tp.getTabs().size() == 0) {
             //���� ���θ����.(�޴��� ���� ex)������,�Ľ�Ÿ..)
             tab = new Tab(m.getCategory());
             //�� �ȿ� �� �÷ο�����
             fp = new FlowPane(5D,0);
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
               fp = new FlowPane(5D, 0);
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
	         node.setMargin(PaymentMenuBtn, new Insets(5,0,0,0));
	         PaymentMenuBtn.setText(name);
	         Label PaymentMenuPrice = (Label)node.lookup("#PaymentMenuPrice");
	         PaymentMenuPrice.setText(price);
	         
	         //�� ��ư�� �׼��� ������ �� �ִ� ������ �Ѵ�.
	         PaymentMenuBtn.setOnAction( e -> {
	            nodeAction(e, PaymentMenuBtn.getText());
	            client.priceUpdate();
	         });
	         
	         return node;
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	      return node;
	   }
	   
	   //TablePaymentController�� ���� Ŭ���̾�Ʈ ������ �Ѱܹ޴´�.(serverController�� 
	   //Client �޼��带 ����ϱ� ����)
	   public void setOrderListAndTable(Client c) {
	      client = c;
	   }
	   
	   private void nodeAction(ActionEvent event, String Name) {
	      flag = false;
	      //�� �޴� ����Ʈ����
	      for(Menu m : menuList) {
	         //���� Ŭ���� ��ư�� �̸��� ���� �޴��� �ִ��� ã�´�.(��ȿ�� �˻�)
	         if(m.getName().equals(Name)) {
	            //Ŭ���̾�Ʈ�� �����޴�����Ʈ�� ����� ���� ���ο� �����޴��� ���� ����Ʈ�� �߰��Ѵ�.
	            if(client.orderMenu_list.size() == 0) {
	               OrderMenu orderMenu = new OrderMenu(m.getName(), 1, Integer.parseInt(m.getPrice()));
	               client.orderMenu_list.add(orderMenu);
	               return;
	            }else {
	               //������� ���� ���� ���� Ŭ���� ��ư�� �̸��� ������ �ִ��� ã�´�.
	               for(OrderMenu om : client.orderMenu_list) {
	                  //������ �� �����޴��� ���� +1
	                  if(Name.equals(om.getName())) {
	                     om.setCnt(om.getCnt() + 1);
	                     flag = true;
	                     return;
	                  }
	               }
	               //�� ã�ƺôµ� ���� ���� ���ο� �����޴��� ����� �ִ´�.
	               if(flag == false) {
	                  OrderMenu orderMenu = new OrderMenu(m.getName(), 1, Integer.parseInt(m.getPrice()));
	                  client.orderMenu_list.add(orderMenu);
	               }
	               flag = false;
	            }
	         }
	      }
	   }
	}