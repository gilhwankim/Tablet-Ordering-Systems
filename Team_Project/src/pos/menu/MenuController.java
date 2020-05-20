package pos.menu;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MenuController implements Initializable{

	@FXML ChoiceBox<String> choiceBox;
	@FXML TableView<Menu> table;
	@FXML TextField tfName;
	@FXML TextField tfPrice;
	@FXML Button btnAdd;
	@FXML Button btnDel;
	
	private ObservableList<Menu> menuList = FXCollections.observableArrayList();//tableView에 연동될 리스트
	private ObservableList<String> col = FXCollections.observableArrayList();	//choiceBox에 연동될 리스트
	private MenuDAO dao = MenuDAO.getinstance();	//DB
	
	private String no;
	private String name;
	private String price;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateTable();
		table.setItems(menuList);
		//테이블 칼럼과 매핑
		TableColumn<Menu, ?> toNo = table.getColumns().get(0);
		toNo.setCellValueFactory(new PropertyValueFactory<>("no"));
		toNo.setStyle("-fx-aliment : CENTER");
		
		TableColumn<Menu, ?> toName = table.getColumns().get(1);
		toName.setCellValueFactory(new PropertyValueFactory<>("name"));
		toName.setStyle("-fx-aliment : CENTER");
		
		TableColumn<Menu, ?> toPrice = table.getColumns().get(2);
		toPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		toPrice.setStyle("-fx-aliment : CENTER");
		
		//choiceBox에 아이템 추가.
		choiceBox.setItems(col);
		col.addAll("파스타", "스테이크","필라프","피자","샐러드", "음료", "술","기타");
		//첫번째 아이템 선택
		choiceBox.getSelectionModel().selectFirst();
		
		//가격은 숫자만 받는걸로
		tfPrice.textProperty().addListener( (ob, olds, news) -> {
			if (!news.matches("\\d*")) {
				tfPrice.setText(news.replaceAll("[^\\d]", ""));
	        }
		});
		//추가 버튼
		btnAdd.setOnAction( e -> btnAddAction(e));
		//삭제 버튼
		btnDel.setOnAction( e -> btnDelAction(e));
	}
	
	private void updateTable() {
		//DB에서 메뉴를 가져온다.
		menuList.clear();
		List<Menu> list = dao.selectAll();
		for(Menu m : list) {
			menuList.add(m);
		}
	}
	private void btnAddAction(ActionEvent event) {
		//추가 버튼을 눌렀을 때 텍스트필드의 정보로
		//메뉴 객체를 만들어 DB에 insert
		no = choiceBox.getSelectionModel().getSelectedItem();
		name = tfName.getText();
		price = tfPrice.getText();
		if(!no.equals(null) && !name.equals("") && !price.equals("")) {
			Menu menu = new Menu(no, name, price);
			dao.insert(menu);
			tfName.clear();
			tfPrice.clear();
			updateTable();
		}
	}
	
	private void btnDelAction(ActionEvent event) {
		String name = table.getSelectionModel().getSelectedItem().getName();
		dao.delete(name);
		updateTable();
	}
}
