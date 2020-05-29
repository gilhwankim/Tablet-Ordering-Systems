package pos.tablepayment;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pos.OrderMenu;
import pos.ServerController.Client;
import pos.menu.Menu;

public class TablePaymentController  {
   
   private Stage stage;            //TablePayment 창 스테이지 
   private List<Menu> menu_list;   //서버에서 넘어오는 전체 메뉴 리스트
   
   private TableView<OrderMenu> tableView;   //TablePayment 창의 테이블 뷰
   private Label payTotal;               //합계 금액
   private Label tNo; //해당 테이블 번호
   
   private Client c;                  //각 테이블에서 넘어오는 클라이언트
   
   private TabPane tp;
   
   private  MakeTab mt;
   private Payment p; //결제화면
   
   //서버 최초 실행시 TablePaymentController생성자 호출하고 초기화한다.
   @SuppressWarnings("unchecked")
   public TablePaymentController(List<Menu> menu) {
     //생성자 호출시 서버에서 총 메뉴 리스트를 받아온다.
      this.menu_list = menu;
      
      //TablePayment.fxml 제일 부모 노드가 Hbox 이므로 hbox로 받아준다.
      HBox hbox = null;
      try {
         stage = new Stage();
         hbox = FXMLLoader.load(getClass().getResource("TablePayment.fxml"));
         tableView = (TableView<OrderMenu>)hbox.lookup("#tableView");
         tp = (TabPane)hbox.lookup("#tp");
         Button plus = (Button)hbox.lookup("#plus");
         Button minus = (Button)hbox.lookup("#minus");
         Button payCash = (Button)hbox.lookup("#payCash");
         Button payCard = (Button)hbox.lookup("#payCard");
         Button order = (Button)hbox.lookup("#payOrder");
         tNo = (Label)hbox.lookup("#tableNo");
         payTotal = (Label)hbox.lookup("#payTotal");

         //테이블뷰 칼럼 매칭
         TableColumn<OrderMenu, ?> a = tableView.getColumns().get(0);
         a.setCellValueFactory(new PropertyValueFactory<>("name"));
         
         TableColumn<OrderMenu, ?> b = tableView.getColumns().get(1);
         b.setCellValueFactory(new PropertyValueFactory<>("price"));
         
         TableColumn<OrderMenu, ?> c = tableView.getColumns().get(2);
         c.setCellValueFactory(new PropertyValueFactory<>("cnt"));
         
         //OrderMenu.java 가보면 getTotal() 은 getPrice * getCnt 되있어서
         //total 금액 부를 때 마다 단가 * 개수 계산하여서 받는다.
         TableColumn<OrderMenu, ?> d = tableView.getColumns().get(3);
         d.setCellValueFactory(new PropertyValueFactory<>("total"));
         
         //버튼들의 동작
         plus.setOnAction( e -> plusAction(e));
         minus.setOnAction( e -> minusAction(e));
         
         //버튼 동작시 결제화면
         p = new Payment();
         payCash.setOnAction((event)-> p.cashShow()); //현금결제 화면 버튼
         payCard.setOnAction((event)-> p.cardShow()); //카드결제 화면 버튼
         
         //TabPane 셋팅
         mt = new MakeTab();
         
         //주문하기 누르면 주방으로 새로 주문한 오더 전송
         order.setOnAction(e->{
        	 //포스기기(TablePayment 화면)에서 주문한 메뉴 리스트
        	 List<OrderMenu> list = mt.getOrderBoardList(e);
        	 if(list.size()!=0) {
        		 String msg = "";
        		 for(OrderMenu m : list) {
        		 msg += m.getName() + "$$" + m.getCnt() + "$$" + m.getPrice();
        		 msg += "@@";
              }
        		 System.out.println("제발요"+msg);
        		 this.c.sendOrderInfo(msg);
        		 mt.listClearplz();
        	 }
         });
         tp = mt.make(menu_list, tp);
         
         Scene scene = new Scene(hbox);
         stage.setScene(scene);
         
      } catch (Exception e) {
         e.printStackTrace();
         
      }
   }
   //서버 클라리언트 단에서 show(...)를 부르면 클라이언트와 클라이언트의 테이블뷰를 받는다.
   public void show(int tableNo, Client client) {
     this.c = client;
      //TablePayment 창의 테이블 뷰에 클라리언트의 테이블뷰를 입력시킨다.
      this.tableView.setItems(c.orderMenu_list);
      //TablePayment 창의 총 합계금액 업데이트
      this.priceUpdate();
      
      //TablePayment에 해당 테이블 번호를 불러온다.
      tNo.textProperty().bind(new SimpleIntegerProperty(tableNo).asString());
      System.out.println("테이블" + tableNo);
      
      //stage.show()
      Platform.runLater( () -> stage.show());
      mt.setOrderListAndTable(c);
   }
   
   private void plusAction(ActionEvent event) {
      //현재 테이블뷰의 아이템이 없을때 누르면 nullpointException 발생하니까 차단
      if(tableView.getItems().size() == 0)
         return;
      //아이템이 있어도 선택안하고 누르면 nullpointException
      if(tableView.getSelectionModel().getSelectedItem() == null)
         return;
      
      //선택된 메뉴의 이름을 받아온다.
      String name = tableView.getSelectionModel().getSelectedItem().getName();
      
      //오더메뉴 리스트에서 선택된 메뉴를 찾는다.
      for(OrderMenu om : c.orderMenu_list) {
         if(om.getName().equals(name)) {
            //찾아서 개수 +1
            om.setCnt(om.getCnt() + 1);
            System.out.println(om.getCnt());
            //각 테이블뷰를 업데이트한다.
            tableView.refresh();
            //pos기 각 테이블의 합계금액 업데이트
            c.priceUpdate();
            break;
         }
      }
   }
   
   private void minusAction(ActionEvent event) {
      if(tableView.getItems().size() == 0)
         return;
      if(tableView.getSelectionModel().getSelectedItem() == null)
         return;
      
      String name = tableView.getSelectionModel().getSelectedItem().getName();
      
      //메뉴 개수가 1일때 '-' 버튼을 누르면 0이되고 삭제해야하므로
      //예외발생을 대비해 Iterator를 쓴다. 
      Iterator<OrderMenu> it = c.orderMenu_list.iterator();
      while(it.hasNext()) {
         OrderMenu om = it.next();
         if(om.getName().equals(name)) {
            if(om.getCnt() > 1) {
               //개수가 2 이상일 때는 개수 -1
               om.setCnt(om.getCnt() - 1);
            }else {
               //1 이하일 때는 삭제한다.
               c.orderMenu_list.remove(om);
            }
            System.out.println(om.getCnt());
            
            //각 테이블뷰를 업데이트한다.
            tableView.refresh();
            //pos기 각 테이블의 합계금액 업데이트
            c.priceUpdate();
            
            break;
         }
      }
   }
   
   //합계금액을 다시 계산해서 업데이트한다.
   public void priceUpdate() {
     Platform.runLater( () -> {
       this.tableView.refresh();
        int total = 0;
        if(this.c != null) {
            for(OrderMenu om : this.c.orderMenu_list) {
               total += om.getTotal();
            }
             payTotal.setText("총금액 : " + total + "원");
        }
      });
   }
}