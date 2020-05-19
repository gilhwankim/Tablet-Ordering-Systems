package kitchen;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class OrderBoardMenu {
	
	private SimpleIntegerProperty tableNum;
	private SimpleStringProperty order;
	
	public OrderBoardMenu(int tableNum, String order ) {
		this.tableNum = new SimpleIntegerProperty(tableNum);
		this.order = new SimpleStringProperty(order);
	}

	public SimpleIntegerProperty getTableNum() {
		return tableNum;
	}

	public void setTableNum(SimpleIntegerProperty tableNum) {
		this.tableNum = tableNum;
	}

	public SimpleStringProperty getOrder() {
		return order;
	}

	public void setOrder(SimpleStringProperty order) {
		this.order = order;
	}
}