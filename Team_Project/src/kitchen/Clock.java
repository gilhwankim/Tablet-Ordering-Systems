package kitchen;

import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Clock {   
	
	public Clock(Label date, Label time) {
		start(date, time);
		
	}
	
   //����ð� �󺧿� �ǽð����� �ð��� ����
   public void start(Label date, Label time) {
   Thread clock = new Thread(()->{
      while(true) {
      SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy�� MM�� d�� E����");
      SimpleDateFormat sdfTime = new SimpleDateFormat("aa hh:mm:ss");      
      
      Platform.runLater(()->{
         //���糯¥ ��
    	  date.setText(sdfDate.format(new Date()));
         //����ð� ��
    	  time.setText(sdfTime.format(new Date()));
      });            
      try {
         Thread.sleep(1000);
      } catch (Exception e) {}
      }
   });
   //���󽺷��� ����
   clock.setDaemon(true);
   clock.start();
   }
}