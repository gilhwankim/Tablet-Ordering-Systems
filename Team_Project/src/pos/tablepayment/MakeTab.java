package pos.tablepayment;

import java.util.ArrayList;
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
   private List<OrderMenu> toOrderBoard = new ArrayList<OrderMenu>();
   private Client client;
   
   public MakeTab() {}
    
   public TabPane make(List<Menu> list, TabPane tabPane) {
      TabPane tp = tabPane;
      menuList = list;
      try {
      for(Menu m : menuList) {
         //탭이 한개도 없을 떄
         if(tp.getTabs().size() == 0) {
             //탭을 새로만든다.(메뉴의 종류 ex)샐러드,파스타..)
             tab = new Tab(m.getCategory());
             //탭 안에 들어갈 플로우페인
             fp = new FlowPane(5D,0);
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
   
   //Flowpane안에 들어갈 버튼들 각 메뉴의 이름과 가격을 가지고 생성한다.
   private StackPane node(String name, String price) {
	      StackPane node = null;
	      try {
	         node = FXMLLoader.load(getClass().getResource("TablePaymentMenuBtn.fxml"));
	         Button PaymentMenuBtn = (Button)node.lookup("#PaymentMenuBtn");
	         node.setMargin(PaymentMenuBtn, new Insets(5,0,0,0));
	         PaymentMenuBtn.setText(name);
	         Label PaymentMenuPrice = (Label)node.lookup("#PaymentMenuPrice");
	         PaymentMenuPrice.setText(price);
	         
	         //각 버튼의 액션을 정해준 뒤 넣는 순서로 한다.
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
	   
	   //TablePaymentController로 부터 클라이언트 정보를 넘겨받는다.(serverController의 
	   //Client 메서드를 사용하기 위해)
	   public void setOrderListAndTable(Client c) {
	      client = c;
	   }
	   
	   private void nodeAction(ActionEvent event, String Name) {
	      flag = false;
	      //총 메뉴 리스트에서
	      for(Menu m : menuList) {
	         //내가 클릭한 버튼의 이름과 같은 메뉴가 있는지 찾는다.(유효성 검사)
	         if(m.getName().equals(Name)) {
	            //클라이언트의 오더메뉴리스트가 비었을 때는 새로운 오더메뉴를 만들어서 리스트에 추가한다.
	            if(client.orderMenu_list.size() == 0) {
	               OrderMenu orderMenu = new OrderMenu(m.getName(), 1, Integer.parseInt(m.getPrice()));
	               client.orderMenu_list.add(orderMenu);
	               addOrderBoardList(orderMenu);
	               return;
	            }else {
	               //비어있지 않을 때는 내가 클릭한 버튼의 이름과 같은게 있는지 찾는다.
	               for(OrderMenu om : client.orderMenu_list) {
	                  //있으면 그 오더메뉴의 개수 +1
	                  if(Name.equals(om.getName())) {
	                	 addOrderBoardList(om);
	                     om.setCnt(om.getCnt() + 1);
	                     flag = true;
	                     return;
	                  }
	               }
	               //다 찾아봤는데 없을 때는 새로운 오더메뉴를 만들어 넣는다.
	               if(flag == false) {
	                  OrderMenu orderMenu = new OrderMenu(m.getName(), 1, Integer.parseInt(m.getPrice()));
	                  client.orderMenu_list.add(orderMenu);
	                  addOrderBoardList(orderMenu);
	               }
	               flag = false;
	               return;
	            }
	         }
	      }
	   }
	   //주방으로 보낼 리스트에 메뉴 추가
	   public void addOrderBoardList(OrderMenu orderMenu) {
		   boolean kitchenFlag = false;
		   //주방으로 보낼 리스트 목록 검사
		   for(OrderMenu om : toOrderBoard) {
			   //이미 주문한 메뉴인 경우
			   if(om.getName().equals(orderMenu.getName())) {
				   om.setCnt(om.getCnt()+1);
				   for(OrderMenu dd : toOrderBoard) {
					   System.out.println(dd.getName()+","+ dd.getCnt());
				   }
				   kitchenFlag = true;
				   return;
			   }
		   }
		   
		   //새로 추가한 메뉴인 경우
		   if(kitchenFlag==false) {
			   //
			   OrderMenu omm = new OrderMenu(orderMenu.getName(), 1, orderMenu.getPrice());
			   toOrderBoard.add(omm);
			   for(OrderMenu dd : toOrderBoard) {
				   System.out.println(dd.getName()+","+ dd.getCnt());
			   }
		   }
		   kitchenFlag=false;
		   return;
	   }
	   
	   public List<OrderMenu> getOrderBoardList(ActionEvent e){
		   return toOrderBoard;
	   }
	   
	   public void listClearplz() {
		   toOrderBoard.clear(); 
	   }
	   
	}