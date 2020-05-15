package noteee;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TaController implements Initializable{
	@FXML TableView<TableData> tableview;
	@FXML TableColumn<TableData, String> name;
	@FXML TableColumn<TableData, Integer> quantity; 
	@FXML Button add;
	
	TableData td = new TableData();
	
	ObservableList<TableData> ob = FXCollections.observableArrayList();
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
	}
	
	public void adddd(ActionEvent event) {
		td.setQuantity(1);
		name.setCellValueFactory(cellData ->cellData.getValue().getName());
		
		tableview.getItems().addAll(td);
		
	}
	
	
	
	
}
