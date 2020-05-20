package pos.menu;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Menu {
	
	private SimpleIntegerProperty menuNum;
	private SimpleStringProperty category;
	private SimpleStringProperty name;
	private SimpleStringProperty price;
	
	public Menu() {
		
	}

	public Menu(int menuNum, String category, String name, String price) {
		super();
		this.menuNum = new SimpleIntegerProperty(menuNum);
		this.category = new SimpleStringProperty(category);
		this.name = new SimpleStringProperty(name);
		this.price = new SimpleStringProperty(price);
	}

	public SimpleIntegerProperty getMenuNum() {
		return menuNum;
	}

	public void setMenuNum(SimpleIntegerProperty menuNum) {
		this.menuNum = menuNum;
	}

	public SimpleStringProperty getCategory() {
		return category;
	}

	public void setCategory(SimpleStringProperty category) {
		this.category = category;
	}

	public SimpleStringProperty getName() {
		return name;
	}

	public void setName(SimpleStringProperty name) {
		this.name = name;
	}

	public SimpleStringProperty getPrice() {
		return price;
	}

	public void setPrice(SimpleStringProperty price) {
		this.price = price;
	}
	

	
	
}
