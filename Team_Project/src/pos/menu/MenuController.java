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
import javafx.scene.input.KeyCode;

public class MenuController implements Initializable{

	@FXML ChoiceBox<String> choiceBox;
	@FXML TableView<Menu> table;
	@FXML TextField tfNum;
	@FXML TextField tfName;
	@FXML TextField tfPrice;
	@FXML Button btnAdd;
	@FXML Button btnDel;
	
	private ObservableList<Menu> menuList = FXCollections.observableArrayList();//tableView에 연동될 리스트
	private ObservableList<String> col = FXCollections.observableArrayList();	//choiceBox에 연동될 리스트
	private MenuDAO dao = MenuDAO.getinstance();	//DB
	
	private int num;
	private String category;
	private String name;
	private String price;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateTable();
		table.setItems(menuList);
		//테이블 칼럼과 매핑
		TableColumn<Menu, ?> toNum = table.getColumns().get(0);
		toNum.setCellValueFactory(new PropertyValueFactory<>("menuNum"));
		toNum.setStyle("-fx-aliment : CENTER");
		
		TableColumn<Menu, ?> toCategory = table.getColumns().get(1);
		toCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
		toCategory.setStyle("-fx-aliment : CENTER");
		
		TableColumn<Menu, ?> toName = table.getColumns().get(2);
		toName.setCellValueFactory(new PropertyValueFactory<>("name"));
		toName.setStyle("-fx-aliment : CENTER");
		
		TableColumn<Menu, ?> toPrice = table.getColumns().get(3);
		toPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
		toPrice.setStyle("-fx-aliment : CENTER");
		
		//choiceBox에 아이템 추가.
		choiceBox.setItems(col);
		col.addAll("파스타", "스테이크","필라프","피자","샐러드", "음료", "주류","기타");
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
		
		//del 키 누르면 삭제
	    table.setOnKeyReleased( e -> {
	         if(e.getCode() == KeyCode.DELETE) {
	            btnDelAction(new ActionEvent());
	         }
	     });
	   
	    //enter키 누르면 삽입
		tfNum.setOnKeyReleased( e -> {
			if(e.getCode() == KeyCode.ENTER) {
		       btnAddAction(new ActionEvent());
			}
		});
		tfName.setOnKeyReleased( e -> {
		    if(e.getCode() == KeyCode.ENTER) {
		       btnAddAction(new ActionEvent());
		    }
		});
		tfPrice.setOnKeyReleased( e -> {
		    if(e.getCode() == KeyCode.ENTER) {
		       btnAddAction(new ActionEvent());
		   }
		});
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
		if(tfNum.getText().equals("")||tfName.getText().equals("")||tfPrice.getText().equals("")) {
			return;
		}
		num = Integer.parseInt(tfNum.getText());
		category = choiceBox.getSelectionModel().getSelectedItem();
		name = tfName.getText();
		price = tfPrice.getText();
		if(!category.equals(null) && !name.equals("") && !price.equals("")) {
			
			Menu menu = new Menu(num,category, name, price);
			dao.insert(menu);
			tfNum.clear();
			tfName.clear();
			tfPrice.clear();
			updateTable();
		}
	}
	
	private void btnDelAction(ActionEvent event) {
		if(table.getSelectionModel().getSelectedItem()==null) {
			return;
		}
		String name = table.getSelectionModel().getSelectedItem().getName();
		dao.delete(name);
		table.getItems().remove(table.getSelectionModel().getSelectedItem());
		updateTable();
	}
}
