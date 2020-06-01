package pos.menu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {

	private static final String ID = "root";
	private static final String PW = "1234";
	private static final String URL = "jdbc:mysql://localhost:3306/posdb";
	private static Connection conn;
	
	public static MenuDAO instance;
	
	public MenuDAO() {
		  try {
	            Class.forName("com.mysql.jdbc.Driver"); 
	            conn = DriverManager.getConnection(URL, ID, PW);
	            System.out.println("드라이버 로딩 성공!!");
	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("드라이버 로드 실패!!");
	        }
	}
	 public static MenuDAO getinstance() {
	    	if(instance == null) {
	    		instance = new MenuDAO();
	    	}
	    	return instance;
	    }	 
	
	  public static Connection getConn() {
		return conn;
	}
	public void insert(Menu menu) {
	       String sql = "insert into menutbl values(?,?,?,?);";
	       PreparedStatement pstmt = null;
	        try {
	            pstmt = conn.prepareStatement(sql);
	            pstmt.setInt(1, menu.getMenuNum());  
	            pstmt.setString(2, menu.getCategory());  
	            pstmt.setString(3, menu.getName()); 
	            pstmt.setString(4, menu.getPrice());
	            pstmt.executeUpdate();
	            System.out.println("데이터 삽입 성공!");
	        } catch (Exception e) {            
	           System.out.println("데이터 삽입 실패!");
	        } finally {
	            try {
	                if (pstmt != null && !pstmt.isClosed())
	                    pstmt.close();
	            } catch (SQLException e) {                
	                e.printStackTrace();
	            }
	        }
	    }
	  
	  public void delete(String name) {
		  String sql = "delete from menutbl where mname = ?;";
		  PreparedStatement pstmt = null;
		  try {
	            pstmt = conn.prepareStatement(sql);
	            pstmt.setString(1, name);  
	            pstmt.executeUpdate();
	            System.out.println("데이터 삭제 성공!");
	        } catch (Exception e) {            
	           System.out.println("데이터 삭제 실패!");
	        } finally {
	            try {
	                if (pstmt != null && !pstmt.isClosed())
	                    pstmt.close();
	            } catch (SQLException e) {                
	                e.printStackTrace();
	            }
	        }
	  }
	  
	  public List<Menu> selectAll() {
	       String sql = "select * from menutbl;";
	        PreparedStatement pstmt = null; 
	        List<Menu> list = new ArrayList<Menu>();
	        try {
	            pstmt = conn.prepareStatement(sql);
	            ResultSet rs = pstmt.executeQuery();
	 
	            while (rs.next()) {   //가져올게 있느냐?
	            	Menu m = new Menu(rs.getInt(1),rs.getString(2), rs.getString(3), rs.getString(4));
	                list.add(m);   //List<Student>에다가 추가함.
	            } 
	        } catch (SQLException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (pstmt != null && !pstmt.isClosed())
	                    pstmt.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	        return list;
	    }
	    
	
}
