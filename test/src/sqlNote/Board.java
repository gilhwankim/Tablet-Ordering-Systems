package sqlNote;

//JAVA Bean이라고 함.
public class Board {
	private int id;
    private String boardTitle;
    private String boardPassword;
    private String comboPublic;
    private String writerName;
    private String textContent;
	
    public Board() {
	
	}
    
    public Board(String boardTitle, String boardPassword, String comboPublic, String writerName, String textContent) {
		this.boardTitle = boardTitle;
		this.boardPassword = boardPassword;
		this.comboPublic = comboPublic;
		this.writerName = writerName;
		this.textContent = textContent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBoardTitle() {
		return boardTitle;
	}

	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}

	public String getBoardPassword() {
		return boardPassword;
	}

	public void setBoardPassword(String boardPassword) {
		this.boardPassword = boardPassword;
	}

	public String getComboPublic() {
		return comboPublic;
	}

	public void setComboPublic(String comboPublic) {
		this.comboPublic = comboPublic;
	}

	public String getWriterName() {
		return writerName;
	}

	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

    
}
