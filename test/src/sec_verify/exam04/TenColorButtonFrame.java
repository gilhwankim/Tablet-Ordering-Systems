package sec_verify.exam04;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class TenColorButtonFrame  extends Frame {

	public TenColorButtonFrame(String title) {
		super("Ten Color Buttons Frame");
		this.setLayout(new GridLayout(1, 10));
		
		Button[] btn = new Button[10];
		Color [] color = { 
							Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
							Color.CYAN, Color.BLUE, Color.MAGENTA, Color.GRAY,
							Color.PINK, Color.LIGHT_GRAY
						 }; 
		for(int i=0; i<btn.length; i++) {
			btn[i] = new Button(Integer.toString(i));			
			btn[i].setBackground(color[i]);
			this.add(btn[i]);
		}
		setSize(500,200);
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
