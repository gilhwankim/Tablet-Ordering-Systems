package sec_verify.exam05;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class Plate4x4Frame extends Frame {
	
	Label [] label = new Label[16];
	Color [] color = {
						Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN,
						Color.CYAN, Color.BLUE, Color.MAGENTA, Color.GRAY,
						Color.PINK, Color.LIGHT_GRAY, Color.WHITE, Color.DARK_GRAY,
						Color.BLACK, Color.ORANGE, Color.BLUE,Color.MAGENTA
					 }; 
	
	public Plate4x4Frame(String title) {
		super("4x4 Color Frame");
		
		this.setLayout(new GridLayout(4, 4));
		for(int i=0; i<label.length; i++) {
			label[i] = new Label(Integer.toString(i));
			label[i].setBackground(color[i]);
			this.add(label[i]);
		}
		this.setSize(500,200);
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
