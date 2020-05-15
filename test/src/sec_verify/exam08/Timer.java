package sec_verify.exam08;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timer extends Frame {
	
	SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
	
	
	public Timer() {
		super("");
		MyEventHandler handler = new MyEventHandler();
		addWindowListener(handler);
		setBounds(500, 200, 400, 100);
		updateClock();
		setResizable(false);
		setVisible(true);
	}
	
	// Frame의 title에 현재시간을 표시한다.
	public void updateClock() { 
		Date currentTime = new Date(System.currentTimeMillis());
		setTitle(sdf.format(currentTime));
	}
	
	public void start() {
		Thread t = new Thread(new Runnable(){
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(1000); // 1초 쉰다.
					} catch(Exception e) {}
					System.out.println("111");
					updateClock();
					
				}
			} // run()
		});
		
		t.start();
	}
	class MyEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			e.getWindow().setVisible(false);
			e.getWindow().dispose();
			System.exit(0);
		}
	} // MyEventHandler
}
		

	
	
	
	
	
