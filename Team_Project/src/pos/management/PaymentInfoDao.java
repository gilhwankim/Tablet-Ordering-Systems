package pos.management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentInfoDao {
   private Connection conn;   
    private static final String USERNAME = "root";   
    private static final String PASSWORD = "1234";   
    private static final String URL = "jdbc:mysql://localhost:3306/posdb";

    public PaymentInfoDao() {
        try {
            System.out.println("������"); 
            Class.forName("com.mysql.jdbc.Driver"); 
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("����̹� �ε� ����!!");            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("����̹� �ε� ����!!");
        }
    }    
    //DB�� �����͸� �����ϴ� �޼���
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
            System.out.println("paymentInfo������ ���� ����!");
        } catch (SQLException e) {            
           System.out.println("paymentInfo������ ���� ����!");
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
    
    //���ǿ� �´� ���� DB���� 1�� �ุ �������� �޼���
    public PaymentInfo selectOne(String paymentMethod) {
        String sql = "select * from paymentinfotbl where cardNum = ? or cash = ?;";
        PreparedStatement pstmt = null;
        PaymentInfo re = new PaymentInfo();
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, paymentMethod);              
            ResultSet rs = pstmt.executeQuery();
            //select�� ����� ResultSet�� ��� ���ϵȴ�.
            if (rs.next()) {  //������ ���� ������ true, ������ false               
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

    public List<PaymentInfo> selectAll() {
       
       String sql = "select * from paymentinfotbl;";
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