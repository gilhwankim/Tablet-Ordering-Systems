package makeSound;

import java.io.File;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class MakeSound extends Thread{
	Boolean stop = false; //재생 중지 시킬 변수
	
	@Override
	public void run() { //start() 호출로 실행
		while(!stop) { 
			try {
				FileInputStream	fis = new FileInputStream("notice.mp3"); //음원 파일 경로
				Player player = new Player(fis);
				player.play(); //플레이어 실행
				Thread.sleep(2000); //정지 안시키면 2초마다 음원 재생  
				System.out.println("효과음 재생 중");
			} catch (Exception e) {
				e.printStackTrace();
			}
			}
		System.out.println("효과음 정지");
		}
	//?占쏙옙怨쇱쓬 以묕옙? ?占쏙옙?占쏙옙 硫붿꽌?占쏙옙
	public void soundStop(Boolean stop) {
		this.stop = stop;
	}
}
