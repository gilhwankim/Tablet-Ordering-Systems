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
   
   private Stage stage;            //TablePayment â �������� 
   private List<Menu> menu_list;   //�������� �Ѿ���� ��ü �޴� ����Ʈ
   
   private TableView<OrderMenu> tableView;   //TablePayment â�� ���̺� ��
   private Label payTotal;               //�հ� �ݾ�
   private Label tNo; //�ش� ���̺� ��ȣ
   
   private Client c;                  //�� ���̺��� �Ѿ���� Ŭ���̾�Ʈ
   
   private TabPane tp;
   
   private  MakeTab mt;
   private Payment p; //����ȭ��
   
   //���� ���� ����� TablePaymentController������ ȣ���ϰ� �ʱ�ȭ�Ѵ�.
   @SuppressWarnings("unchecked")
   public TablePaymentController(List<Menu> menu) {
     //������ ȣ��� �������� �� �޴� ����Ʈ�� �޾ƿ´�.
      this.menu_list = menu;
      
      //TablePayment.fxml ���� �θ� ��尡 Hbox �̹Ƿ� hbox�� �޾��ش�.
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

         //���̺�� Į�� ��Ī
         TableColumn<OrderMenu, ?> a = tableView.getColumns().get(0);
         a.setCellValueFactory(new PropertyValueFactory<>("name"));
         
         TableColumn<OrderMenu, ?> b = tableView.getColumns().get(1);
         b.setCellValueFactory(new PropertyValueFactory<>("price"));
         
         TableColumn<OrderMenu, ?> c = tableView.getColumns().get(2);
         c.setCellValueFactory(new PropertyValueFactory<>("cnt"));
         
         //OrderMenu.java ������ getTotal() �� getPrice * getCnt ���־
         //total �ݾ� �θ� �� ���� �ܰ� * ���� ����Ͽ��� �޴´�.
         TableColumn<OrderMenu, ?> d = tableView.getColumns().get(3);
         d.setCellValueFactory(new PropertyValueFactory<>("total"));
         
         //��ư���� ����
         plus.setOnAction( e -> plusAction(e));
         minus.setOnAction( e -> minusAction(e));
         
         //��ư ���۽� ����ȭ��
         p = new Payment();
         payCash.setOnAction((event)-> p.cashShow()); //���ݰ��� ȭ�� ��ư
         payCard.setOnAction((event)-> p.cardShow()); //ī����� ȭ�� ��ư
         
         //TabPane ����
         mt = new MakeTab();
         
         //�ֹ��ϱ� ������ �ֹ����� ���� �ֹ��� ���� ����
         order.setOnAction(e->{
        	 //�������(TablePayment ȭ��)���� �ֹ��� �޴� ����Ʈ
        	 List<OrderMenu> list = mt.getOrderBoardList(e);
        	 if(list.size()!=0) {
        		 String msg = "";
        		 for(OrderMenu m : list) {
        		 msg += m.getName() + "$$" + m.getCnt() + "$$" + m.getPrice();
        		 msg += "@@";
              }
        		 System.out.println("���߿�"+msg);
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
   //���� Ŭ�󸮾�Ʈ �ܿ��� show(...)�� �θ��� Ŭ���̾�Ʈ�� Ŭ���̾�Ʈ�� ���̺�並 �޴´�.
   public void show(int tableNo, Client client) {
     this.c = client;
      //TablePayment â�� ���̺� �信 Ŭ�󸮾�Ʈ�� ���̺�並 �Է½�Ų��.
      this.tableView.setItems(c.orderMenu_list);
      //TablePayment â�� �� �հ�ݾ� ������Ʈ
      this.priceUpdate();
      
      //TablePayment�� �ش� ���̺� ��ȣ�� �ҷ��´�.
      tNo.textProperty().bind(new SimpleIntegerProperty(tableNo).asString());
      System.out.println("���̺�" + tableNo);
      
      //stage.show()
      Platform.runLater( () -> stage.show());
      mt.setOrderListAndTable(c);
   }
   
   private void plusAction(ActionEvent event) {
      //���� ���̺���� �������� ������ ������ nullpointException �߻��ϴϱ� ����
      if(tableView.getItems().size() == 0)
         return;
      //�������� �־ ���þ��ϰ� ������ nullpointException
      if(tableView.getSelectionModel().getSelectedItem() == null)
         return;
      
      //���õ� �޴��� �̸��� �޾ƿ´�.
      String name = tableView.getSelectionModel().getSelectedItem().getName();
      
      //�����޴� ����Ʈ���� ���õ� �޴��� ã�´�.
      for(OrderMenu om : c.orderMenu_list) {
         if(om.getName().equals(name)) {
            //ã�Ƽ� ���� +1
            om.setCnt(om.getCnt() + 1);
            System.out.println(om.getCnt());
            //�� ���̺�並 ������Ʈ�Ѵ�.
            tableView.refresh();
            //pos�� �� ���̺��� �հ�ݾ� ������Ʈ
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
      
      //�޴� ������ 1�϶� '-' ��ư�� ������ 0�̵ǰ� �����ؾ��ϹǷ�
      //���ܹ߻��� ����� Iterator�� ����. 
      Iterator<OrderMenu> it = c.orderMenu_list.iterator();
      while(it.hasNext()) {
         OrderMenu om = it.next();
         if(om.getName().equals(name)) {
            if(om.getCnt() > 1) {
               //������ 2 �̻��� ���� ���� -1
               om.setCnt(om.getCnt() - 1);
            }else {
               //1 ������ ���� �����Ѵ�.
               c.orderMenu_list.remove(om);
            }
            System.out.println(om.getCnt());
            
            //�� ���̺�並 ������Ʈ�Ѵ�.
            tableView.refresh();
            //pos�� �� ���̺��� �հ�ݾ� ������Ʈ
            c.priceUpdate();
            
            break;
         }
      }
   }
   
   //�հ�ݾ��� �ٽ� ����ؼ� ������Ʈ�Ѵ�.
   public void priceUpdate() {
     Platform.runLater( () -> {
       this.tableView.refresh();
        int total = 0;
        if(this.c != null) {
            for(OrderMenu om : this.c.orderMenu_list) {
               total += om.getTotal();
            }
             payTotal.setText("�ѱݾ� : " + total + "��");
        }
      });
   }
}