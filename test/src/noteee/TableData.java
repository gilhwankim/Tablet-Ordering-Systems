package noteee;

import javafx.beans.property.StringProperty;

public class TableData {
	
	private StringProperty name;
	private int quantity;
	
	public StringProperty getName() {
		return name;
	}
	public void setName(StringProperty name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
	
}
