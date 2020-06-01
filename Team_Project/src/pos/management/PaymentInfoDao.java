package pos.management;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pos.menu.MenuDAO;

public class PaymentInfoDao {  
	
	private Connection conn;
	
    public PaymentInfoDao() {
    	conn = MenuDAO.getConn();
    }    
    //DB에 데이터를 저장하는 메서드
    public void insertBoard(PaymentInfo paymentInfo) {
       String sql = "insert into paymentinfotbl values(?,?,?,?,?,?);";
       PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, paymentInfo.getDate());
            pstmt.setString(2, paymentInfo.getAllMenu());
            pstmt.setString(3, paymentInfo.getTotalPrice());
            pstmt.setString(4, paymentInfo.getCardNum());
            pstmt.setString(5, paymentInfo.getCash());
            pstmt.setString(6, paymentInfo.getPayMethod());
            
            pstmt.executeUpdate();
            System.out.println("paymentInfo데이터 삽입 성공!");
        } catch (SQLException e) {
           System.out.println("paymentInfo데이터 삽입 실패!");
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null && !pstmt.isClosed())
                    pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    //조건에 맞는 행을 DB에서 1개 행만 가져오는 메서드
    public PaymentInfo selectOne(String paymentMethod) {
        String sql = "select * from paymentinfotbl where cardNum = ? or cash = ?;";
        PreparedStatement pstmt = null;
        PaymentInfo re = new PaymentInfo();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, paymentMethod);              
            ResultSet rs = pstmt.executeQuery();
            //select한 결과는 ResultSet에 담겨 리턴된다.
            if (rs.next()) {  //가져올 행이 있으면 true, 없으면 false               
                re.setDate(rs.getString("date"));
                re.setAllMenu(rs.getString("allMenu"));
                re.setTotalPrice(rs.getString("totalPrice"));
                re.setCardNum(rs.getString("cardNum"));
                re.setCash(rs.getString("cash"));
                re.setPayMethod(rs.getString("payMethod"));    
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
        return re;
    }
    //해당 날짜의 거래내역을 받아줌
    public List<PaymentInfo> selectDate(String date) {       
       String sql = "select * from paymentinfotbl where date = \"" + date + "\";";
        PreparedStatement pstmt = null; 
        List<PaymentInfo> list = new ArrayList<PaymentInfo>();
        try {
            pstmt = conn.prepareStatement(sql);
            ResultSet re = pstmt.executeQuery();
 
            while (re.next()) {   
               PaymentInfo s = new PaymentInfo();        
                s.setDate(re.getString("date"));
                s.setAllMenu(re.getString("allMenu"));
                s.setTotalPrice(re.getString("totalPrice"));
                s.setCardNum(re.getString("cardNum"));                
                s.setCash(re.getString("cash"));
                s.setPayMethod(re.getString("payMethod"));
                list.add(s); 
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