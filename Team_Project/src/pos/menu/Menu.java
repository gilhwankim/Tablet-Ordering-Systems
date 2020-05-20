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

	public int getMenuNum() {
		return menuNum.get();
	}

	public void setMenuNum(int menuNum) {
		this.menuNum.set(menuNum);
	}

	public String getCategory() {
		return category.get();
	}

	public void setCategory(String category) {
		this.category.set(category);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getPrice() {
		return price.get();
	}

	public void setPrice(String price) {
		this.price.set(price);
	}
	

	
	
}
