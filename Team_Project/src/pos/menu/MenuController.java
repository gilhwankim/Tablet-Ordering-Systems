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
	@FXML TextField tfNum;
	@FXML TextField tfName;
	@FXML TextField tfPrice;
	@FXML Button btnAdd;
	@FXML Button btnDel;
	
	private ObservableList<Menu> menuList = FXCollections.observableArrayList();//tableView�� ������ ����Ʈ
	private ObservableList<String> col = FXCollections.observableArrayList();	//choiceBox�� ������ ����Ʈ
	private MenuDAO dao = MenuDAO.getinstance();	//DB
	
	private int num;
	private String category;
	private String name;
	private String price;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateTable();
		table.setItems(menuList);
		//���̺� Į���� ����
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
		
		//choiceBox�� ������ �߰�.
		choiceBox.setItems(col);
		col.addAll("�Ľ�Ÿ", "������ũ","�ʶ���","����","������", "����", "�ַ�","��Ÿ");
		//ù��° ������ ����
		choiceBox.getSelectionModel().selectFirst();
		
		//������ ���ڸ� �޴°ɷ�
		tfPrice.textProperty().addListener( (ob, olds, news) -> {
			if (!news.matches("\\d*")) {
				tfPrice.setText(news.replaceAll("[^\\d]", ""));
	        }
		});
		//�߰� ��ư
		btnAdd.setOnAction( e -> btnAddAction(e));
		//���� ��ư
		btnDel.setOnAction( e -> btnDelAction(e));
	}
	
	private void updateTable() {
		//DB���� �޴��� �����´�.
		menuList.clear();
		List<Menu> list = dao.selectAll();
		for(Menu m : list) {
			menuList.add(m);
		}
	}
	private void btnAddAction(ActionEvent event) {
		//�߰� ��ư�� ������ �� �ؽ�Ʈ�ʵ��� ������
		//�޴� ��ü�� ����� DB�� insert
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
