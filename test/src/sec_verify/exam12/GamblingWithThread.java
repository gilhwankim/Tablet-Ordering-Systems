package sec_verify.exam12;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class GamblingWithThread extends Frame {
	
	public GamblingWithThread(String title) {
		super("°×ºí¸µ °ÔÀÓ");	
		//System.out.println("11");
		
		GamePanel gp = new GamePanel();
		this.setSize(300, 250);
		this.add(gp);
		//System.out.println("22");
		this.setVisible(true);
		
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});		
	}
}