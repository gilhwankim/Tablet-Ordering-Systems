package pos.tablepayment;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
   
   private Client c;                  //�� ���̺��� �Ѿ���� Ŭ���̾�Ʈ
   private TableView<OrderMenu> t;         //�Ѿ�� Ŭ���̾�Ʈ���� ���̺��
   
   private Button PaymentMenuBtn;
   private Label PaymentMenuPrice;
   private FlowPane SaladGridPane;
   private FlowPane PastaGridPane;
   private FlowPane SteakGridPane;
   private FlowPane PilafGridPane;
   private FlowPane PizzaGridPane;
   private FlowPane AlcoholGridPane;
   private FlowPane DrinkGridPane;
   private FlowPane EtcGridPane;
   
   
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
         Button plus = (Button)hbox.lookup("#plus");
         Button minus = (Button)hbox.lookup("#minus");
         Button payCash = (Button)hbox.lookup("#payCash");
         Button payCard = (Button)hbox.lookup("#payCard");
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
         payCash.setOnAction((event)-> callCash(event)); //���ݰ��� ��ư �޼���
         payCard.setOnAction((event)-> callCard(event)); //ī����� ��ư �޼���
         //�޴� ��ư
         SaladGridPane = (FlowPane)hbox.lookup("#SaladGridPane");
         makeBtn(SaladGridPane);
         
         
         Scene scene = new Scene(hbox);
         stage.setScene(scene);
         
      } catch (Exception e) {
         e.printStackTrace();
         
      }
   }
   //���� Ŭ�󸮾�Ʈ �ܿ��� show(...)�� �θ��� Ŭ���̾�Ʈ�� Ŭ���̾�Ʈ�� ���̺�並 �޴´�.
   public void show(Client client, TableView<OrderMenu> tableView) {
      this.c = client;
      this.t = tableView;
      //TablePayment â�� ���̺� �信 Ŭ�󸮾�Ʈ�� ���̺�並 �Է½�Ų��.
      this.tableView.setItems(c.orderMenu_list);
      //TablePayment â�� �� �հ�ݾ� ������Ʈ
      this.priceUpdate();
      
      //stage.show()
      Platform.runLater( () -> stage.show());
      
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
            //t�� pos�� ���̺��
            t.refresh();
            //pos�� �� ���̺��� �հ�ݾ� ������Ʈ
            c.priceUpdate();
            //���� â�� ���̺� �հ�ݾ� ������Ʈ
            this.priceUpdate();
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
            t.refresh();
            //pos�� �� ���̺��� �հ�ݾ� ������Ʈ
            c.priceUpdate();
            //���� â�� ���̺� �հ�ݾ� ������Ʈ
            this.priceUpdate();
            
            break;
         }
      }
   }
   
   //�հ�ݾ��� �ٽ� ����ؼ� ������Ʈ�Ѵ�.
   public void priceUpdate() {
     this.tableView.refresh();
     Platform.runLater( () -> {
        int total = 0;
        if(this.c != null) {
            for(OrderMenu om : this.c.orderMenu_list) {
               total += om.getTotal();
            }
             payTotal.setText("�ѱݾ� : " + total + "��");
        }
      });
   }
   
   public void makeBtn(FlowPane pane) {
	   try {
		   for(Menu m : menu_list) {
				   StackPane node = FXMLLoader.load(getClass().getResource("TablePaymentMenuBtn.fxml"));
				   PaymentMenuBtn = (Button)node.lookup("#PaymentMenuBtn");
				   PaymentMenuBtn.setText(m.getName());
				   PaymentMenuPrice = (Label)node.lookup("#PaymentMenuPrice");
				   PaymentMenuPrice.setText(m.getPrice());
				   if(m.getCategory().equals("������")) {
					   pane.setHgap(4);
					   pane.getChildren().add(node);
				   }
		   }
	} catch (Exception e) {
		e.printStackTrace();
	}
   }
   
   //���ݰ��� ȭ��
   private void callCash(ActionEvent event) {
      System.out.println("���ݰ���");
      Stage dialog = new Stage(StageStyle.UNDECORATED);
      dialog.initModality(Modality.WINDOW_MODAL); //dialog�� ���(������ ������ ���Ұ�)�� ����
      dialog.initOwner(stage);
      
      try {
         Parent cashPayment = FXMLLoader.load(getClass().getResource("CashPayment.fxml"));
         Scene scene = new Scene(cashPayment);
         dialog.setScene(scene);
         dialog.setResizable(false); //����ڰ� ũ�⸦ �������� ���ϰ� ��
         dialog.show();
         
         //���ݰ��� ȭ�� �ݱ�
         Button cashExitBtn = (Button)cashPayment.lookup("#exit");
         cashExitBtn.setOnMouseClicked(e-> dialog.close());
         
      } catch (IOException e) { e.printStackTrace(); }
   }
   
   //ī����� ȭ��
   private void callCard(ActionEvent event) {
      System.out.println("ī�����");
      Stage dialog = new Stage(StageStyle.UNDECORATED);
      dialog.initModality(Modality.WINDOW_MODAL); //dialog�� ���(������ ������ ���Ұ�)�� ����
      dialog.initOwner(stage);
      
      try {
         Parent cardPayment = FXMLLoader.load(getClass().getResource("PayingCreditCard.fxml"));
         Scene scene = new Scene(cardPayment);
         dialog.setScene(scene);
         dialog.setResizable(false); //����ڰ� ũ�⸦ �������� ���ϰ� ��
         dialog.show();
         
         //ī����� ȭ�� �ݱ�
         Button cardExitBtn = (Button)cardPayment.lookup("#exit");
         cardExitBtn.setOnMouseClicked(e-> dialog.close());
         
      } catch (IOException e) { e.printStackTrace(); }
      
   }
   
}