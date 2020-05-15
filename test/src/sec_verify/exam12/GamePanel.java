package sec_verify.exam12;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends Panel {
	
	Label[] label = new Label[3]; // 3 개의 수를 표현하는 레이블 배열
	Label result = new Label("시작을 클릭하면 게임을 시작합니다!"); // 결과를 출력하는 레이블
	Button btnStart = new Button("시 작");
	Button btnStop = new Button("종 료");
	GamblingThread thread = new GamblingThread(label, result); // 게임 스레드
	
	
	
	public GamePanel() {
		System.out.println("33");
		
		this.setLayout(null); // 배치관리자를 사용하지 않고 절대 위치에 컴포넌트를 배치한다.
		this.setSize(300, 250);
		this.setBackground(Color.BLUE);
		//3 개의 레이블을 생성하여 컨테이너에 단다.
		for(int i=0; i<label.length; i++) {
			label[i] = new Label("0"); // 레이블 생성
			label[i].setSize(60, 30); // 레이블 크기
			label[i].setLocation(30 + 80 * i, 50); // 레이블 위치
			label[i].setAlignment(Label.CENTER); // 레이블에 출력되는 수를 센터링
			
			label[i].setBackground(Color.WHITE); // 레이블의 배경색을 설정한다.
			label[i].setForeground(Color.black); //레이블의 글자 색을 설정한다.				
			label[i].setFont(new Font("Tahoma", Font.ITALIC, 30)); // 레이블 글자의 폰트를 설정한다.	
			this.add(label[i]); // 레이블을 부착한다.
		}
		
		//결과를 출력할 레이블을 생성하고 컨테이너에 부착한다.
		result.setAlignment(Label.CENTER); // 레이블에 출력되는 수를 센터링
		result.setFont(new Font("Tahoma", Font.ITALIC, 15));
		result.setForeground(Color.WHITE);
		result.setSize(250, 50);
		result.setLocation(20, 100);
		
		btnStart.setSize(50, 30);
		btnStart.setLocation(70, 160);
		
		btnStop.setSize(50, 30);
		btnStop.setLocation(150, 160);
		
		this.add(btnStart);
		this.add(btnStop);
		this.add(result);
		
		//System.out.println("44");
		
		thread.start();		
		//버튼에 ActionListener를 구현한다.
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(thread.isReady()) // 스레드가 게임 중이면 그냥 리턴
					thread.startGambling();
				
			}
		});		
		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);				
			}
		});
	}
}