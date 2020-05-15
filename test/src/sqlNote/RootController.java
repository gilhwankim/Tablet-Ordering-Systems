package sqlNote;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RootController implements Initializable {
	
	@FXML TextField txtTitle; //제목
	@FXML PasswordField txtPassword; //비밀번호
	@FXML ComboBox<String> comboPublic; //콤보박스
	@FXML TextArea txtContent; // 내용
	@FXML TextField textField; //조회번호
	@FXML TextField writerName; //작성자
	
	BoarderDAO board = new BoarderDAO();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	//저장
	public void handleBtnRegAction(ActionEvent event) {
		board.insertinfo(new Board(txtTitle.getText(),txtPassword.getText(),
				comboPublic.getSelectionModel().getSelectedItem(),writerName.getText(),txtContent.getText()));
		board.number();
	}
	
	//초기화 
	public void handleBtnClear(ActionEvent event) {
		txtTitle.setText("");
		txtPassword.setText("");
		txtContent.setText("");
		writerName.setText("");
		textField.setText("");
	}
	
	//조회
	public void handleBtnSelect(ActionEvent event) {
		Board boardd = new Board();
		//조회번호 입력
		boardd.setId(Integer.parseInt(textField.getText()));
		board.selectOne(boardd);
		
		txtTitle.setText(boardd.getBoardTitle());
		txtPassword.setText(boardd.getBoardPassword());
		txtContent.setText(boardd.getTextContent());
		writerName.setText(boardd.getWriterName());
		
	}
}