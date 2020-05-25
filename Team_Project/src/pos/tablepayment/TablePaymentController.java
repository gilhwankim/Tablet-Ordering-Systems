package pos.tablepayment;

import java.util.Iterator;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pos.OrderMenu;
import pos.ServerController.Client;
import pos.menu.Menu;

public class TablePaymentController  {
   
   private Stage stage;
   private List<Menu> menu_list;
   
   private TableView<OrderMenu> tableView;
   private Label payTotal;
   
   private Client c;
   private TableView<OrderMenu> t;
   
   @SuppressWarnings("unchecked")
   public TablePaymentController(List<Menu> menu) {
      this.menu_list = menu;
      HBox hbox = null;
      try {
         stage = new Stage();
         hbox = FXMLLoader.load(getClass().getResource("TablePayment.fxml"));
         tableView = (TableView<OrderMenu>)hbox.lookup("#tableView");
         Button plus = (Button)hbox.lookup("#plus");
         Button minus = (Button)hbox.lookup("#minus");
         payTotal = (Label)hbox.lookup("#payTotal");
         
         TableColumn<OrderMenu, ?> a = tableView.getColumns().get(0);
         a.setCellValueFactory(new PropertyValueFactory<>("name"));
         
         TableColumn<OrderMenu, ?> b = tableView.getColumns().get(1);
         b.setCellValueFactory(new PropertyValueFactory<>("price"));
         
         TableColumn<OrderMenu, ?> c = tableView.getColumns().get(2);
         c.setCellValueFactory(new PropertyValueFactory<>("cnt"));
         
         TableColumn<OrderMenu, ?> d = tableView.getColumns().get(3);
         d.setCellValueFactory(new PropertyValueFactory<>("total"));
         
         plus.setOnAction( e -> plusAction(e));
         minus.setOnAction( e -> minusAction(e));
         

         
         Scene scene = new Scene(hbox);
         stage.setScene(scene);
      } catch (Exception e) {
         e.printStackTrace();
         
      }
   }
   public void show(Client client, TableView<OrderMenu> tableView) {
      this.c = client;
      this.t = tableView;
      this.tableView.setItems(c.orderMenu_list);
      this.priceUpdate();
      Platform.runLater( () -> stage.show());
      
   }
   
   private void plusAction(ActionEvent event) {
      if(tableView.getItems().size() == 0)
         return;
      if(tableView.getSelectionModel().getSelectedItem() == null)
         return;
      
      String name = tableView.getSelectionModel().getSelectedItem().getName();
      
      
      for(OrderMenu om : c.orderMenu_list) {
         if(om.getName().equals(name)) {
            om.setCnt(om.getCnt() + 1);
            System.out.println(om.getCnt());
            tableView.refresh();
            t.refresh();
            c.priceUpdate();
            this.priceUpdate();
         }
      }
   }
   
   private void minusAction(ActionEvent event) {
      if(tableView.getItems().size() == 0)
         return;
      if(tableView.getSelectionModel().getSelectedItem() == null)
         return;
      
      String name = tableView.getSelectionModel().getSelectedItem().getName();
      
      Iterator<OrderMenu> it = c.orderMenu_list.iterator();
      while(it.hasNext()) {
         OrderMenu om = it.next();
         if(om.getName().equals(name)) {
            if(om.getCnt() > 1) {
               om.setCnt(om.getCnt() - 1);
            }else {
               c.orderMenu_list.remove(om);
            }
            System.out.println(om.getCnt());
            tableView.refresh();
            t.refresh();
            c.priceUpdate();
            this.priceUpdate();
            break;
         }
      }
      
   }
   
   private void priceUpdate() {
      int total = 0;
      for(OrderMenu om : this.c.orderMenu_list) {
         total += om.getTotal();
      }
      payTotal.setText("ÃÑ±Ý¾× : " + total + "¿ø");
   }
   
   
}