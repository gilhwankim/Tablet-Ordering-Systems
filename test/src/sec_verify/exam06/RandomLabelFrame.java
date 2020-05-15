package sec_verify.exam06;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class RandomLabelFrame extends Frame {
	
	public RandomLabelFrame(String title) {
		super("Random Labels");
		
		this.setLayout(null);
		
		for(int i=0; i<20; i++) {
			Label label = new Label(i + "");
			label.setBackground(Color.BLUE);
			
			int x = (int)(Math.random()*400) + 50;
			int y = (int)(Math.random()*400) + 50;
			
			label.setLocation(x,y);
			label.setSize(30,30);
			this.add(label);
		}
		this.setSize(500,500);
		this.setVisible(true);
		this.addWindowListener(wa);
	}
	
	WindowAdapter wa = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {			
			System.exit(0);
		}
	};
	
	
}
