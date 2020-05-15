package sqlNote;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoarderDAO {
	
	private Connection conn; //db 커넥션 개체
	private static final String USERNAME = "root";
	private static final String PASSWORD = "1234";
	//DB접속 경로 설정 
	
	private static final String URL = "jdbc:mysql://localhost:3306/boarddb";
		
	public BoarderDAO() {

	//connection 객체 생성 후 DB에 연결
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(URL,USERNAME,PASSWORD);
			System.out.println("드라이버 로딩 성공");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("드라이버 로딩 실패");
		}
	}
	
	public void insertinfo(Board board) {
		
		String sql = "insert into board values(?,?,?,?,?,?);";
		
		PreparedStatement pstmt = null;
		try {
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1,null);
		pstmt.setString(2,board.getBoardTitle());
		pstmt.setString(3,board.getBoardPassword());
		pstmt.setString(4,board.getComboPublic());
		pstmt.setString(5,board.getWriterName());
		pstmt.setString(6,board.getTextContent());
		
		//쿼리문 실행
		pstmt.executeUpdate();
		System.out.println("데이터 삽입 성공");
		
		} catch (Exception e) {
		System.out.println("데이터 삽입 실패");
		} finally {
			try {
				if(pstmt != null & !pstmt.isClosed()) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}//finally
		
	}
	
	public void number() {
		//저장 직후 가장 최근 값 불러와서 사용자의 조회번호를 알려준다.
		String sqlId = "select id from board order by id desc limit 1;";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sqlId);
			ResultSet rs = pstmt.executeQuery(sqlId);
			
			while(rs.next()) {
				int id = rs.getInt("id");
				System.out.println("조회 번호는 "+id+"입니다.");
				}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt!=null && !pstmt.isClosed())  {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void selectOne(Board board) {
		String sql = "select * from board where id="+board.getId()+";";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery(sql);
			
			while(rs.next()) {
				String id = rs.getString("borderTitle");
				board.setBoardTitle(id);
				String password = rs.getString("borderPassword");
				board.setBoardPassword(password);
				String writername = rs.getString("writerName");
				board.setWriterName(writername);
				String textcontent = rs.getString("textContent");
				board.setTextContent(textcontent);
			}
			
		} catch (Exception e) {
		} finally {
			try {
				if(pstmt != null & !pstmt.isClosed()) {
					pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}