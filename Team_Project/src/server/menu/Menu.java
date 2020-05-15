package server.menu;

import javafx.beans.property.SimpleStringProperty;

public class Menu {
	
	private SimpleStringProperty no;
	private SimpleStringProperty name;
	private SimpleStringProperty price;
	
	public Menu() {
	}
	
	public Menu(String no, String name, String price) {
		super();
		this.no = new SimpleStringProperty(no);
		this.name = new SimpleStringProperty(name);
		this.price = new SimpleStringProperty(price);
	}
	
	public String getNo() {
		return no.get();
	}
	public void setNo(String no) {
		this.no.set(no);
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
