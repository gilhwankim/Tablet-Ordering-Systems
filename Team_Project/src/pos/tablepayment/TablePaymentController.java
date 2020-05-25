package pos.tablepayment;

import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pos.OrderMenu;
import pos.menu.Menu;

public class TablePaymentController  {
   
   private Stage stage;
   private List<Menu> menu_list;
   
   private TableView<OrderMenu>tableView;
   private ObservableList<OrderMenu> ol;
   
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
         
         
         TableColumn<OrderMenu, ?> a = tableView.getColumns().get(0);
         a.setCellValueFactory(new PropertyValueFactory<>("name"));
         
         TableColumn<OrderMenu, ?> b = tableView.getColumns().get(1);
         b.setCellValueFactory(new PropertyValueFactory<>("price"));
         
         TableColumn<OrderMenu, ?> c = tableView.getColumns().get(2);
         c.setCellValueFactory(new PropertyValueFactory<>("cnt"));
         
         TableColumn<OrderMenu, ?> d = tableView.getColumns().get(3);
         d.setCellValueFactory(new PropertyValueFactory<>("total"));
         
         Scene scene = new Scene(hbox);
         stage.setScene(scene);
      } catch (Exception e) {
         e.printStackTrace();
         
      }
   }
   public void show(ObservableList<OrderMenu> ol) {
      this.ol = ol;
      tableView.setItems(this.ol);
      Platform.runLater( () -> stage.show());
   }
   
   
}