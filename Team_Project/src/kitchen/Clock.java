package kitchen;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Clock {   
	
	public Clock(Label date, Label time) {
		start(date, time);
		
	}
	
   //현재시간 라벨에 실시간으로 시간을 세팅
   public void start(Label date, Label time) {
   Thread clock = new Thread(()->{
      while(true) {
      SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy년 MM월 d일 E요일");
      SimpleDateFormat sdfTime = new SimpleDateFormat("aa hh:mm:ss");      
      
      Platform.runLater(()->{
         //현재날짜 라벨
    	  date.setText(sdfDate.format(new Date()));
         //현재시간 라벨
    	  time.setText(sdfTime.format(new Date()));
      });            
      try {
         Thread.sleep(1000);
      } catch (Exception e) {}
      }
   });
   //데몬스레드 설정
   clock.setDaemon(true);
   clock.start();
   }
}