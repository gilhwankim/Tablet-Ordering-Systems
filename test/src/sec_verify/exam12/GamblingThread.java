package sec_verify.exam12;

import java.awt.Color;
import java.awt.Label;

public class GamblingThread extends Thread {
	
	Label[] label; //게임 숫자를 출력하는 레이블
	Label result;  //결과를 출력하는 레이블
	long delay = 300; //지연 시간의 초깃값 = 200
	boolean gambling = false; //게임을 할 것인지. 초깃값 = false
	
	public GamblingThread(Label[] label, Label result) {
		this.label = label;
		this.result = result;
	}
	
	boolean isReady() {
		return !gambling; //게임 중이면 준비되지 않았음
	}
	
	public synchronized void waitForGambling() {
		if(!gambling) //마우스 클릭에 의해 gambling이 true가 아니면 기다림
			try {
				this.wait();
			} catch (InterruptedException e) {}
	}
	
	public synchronized void startGambling() {
		this.gambling = true; //마우스 클릭으로 스레드가 게임을 진행하도록 지시
		this.notify(); //기다리는 스레드를 깨움
	}		
	@Override
	public void run() {
		while(true) {
			waitForGambling(); //마우스 클릭에 의해 게임 진행 지시를 기다림
			try {
				int x1 = (int)(Math.random()*3) + 1; //1~3까지의 랜덤수
				int x2 = (int)(Math.random()*3) + 1; //1~3까지의 랜덤수
				int x3 = (int)(Math.random()*3) + 1; //1~3까지의 랜덤수
				
				//첫번째 수 조정
				label[0].setForeground(Color.RED); // 글자가 바뀌는 것을 가시화하기 위해
				sleep(delay);
				label[0].setForeground(Color.BLACK);
				label[0].setText(Integer.toString(x1));
				
				//두번째 수 조정					
				label[1].setForeground(Color.RED); // 글자가 바뀌는 것을 가시화하기 위해
				sleep(delay);
				label[1].setForeground(Color.BLACK);					
				label[1].setText(Integer.toString(x2));
				
				//세번째 수 조정
				label[2].setForeground(Color.RED); // 글자가 바뀌는 것을 가시화하기 위해
				sleep(delay);
				label[2].setForeground(Color.BLACK);					
				label[2].setText(Integer.toString(x3));	
				
				// 게임이 성공하였는지 판별
				if(x1 == x2 && x2 == x3)
					result.setText("축하합니다!!");
				else 
					result.setText("아쉽군요");
				gambling = false; // 이제 다음 게임이 가능하도록 설정
			} catch (InterruptedException e) {}
		}
	}	
}


