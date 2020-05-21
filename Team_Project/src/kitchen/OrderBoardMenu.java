package kitchen;

import javafx.beans.property.SimpleStringProperty;

public class OrderBoardMenu {
	
	private SimpleStringProperty menuName;
	private SimpleStringProperty menuCnt;
	//메뉴와 수량 
	
	public OrderBoardMenu(String menuName, String menuCnt) {
		super();
		this.menuName = new SimpleStringProperty(menuName);
		this.menuCnt = new SimpleStringProperty(menuCnt);
	}
	
	public String getMenuName() {
		return menuName.get();
	}

	public void setMenuName(String menuName) {
		this.menuName.set(menuName);;
	}
	public String getMenuCnt() {
		return menuCnt.get();
	}
	public void setMenuCnt(String menuCnt) {
		this.menuCnt.set(menuCnt);;
	}


	
	
	
//	public String getMenuName() {
//		return menuName.get();
//	}
//
//	public void setMenuName(String menuName) {
//		this.menuName.set(menuName);
//	}
//
//	public int getMenuCnt() {
//		return menuCnt.get();
//	}
//
//	public void setMenuCnt(int menuCnt) {
//		this.menuCnt.set(menuCnt);
//	}

}