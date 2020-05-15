package sec_verify.exam03;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class TenButtonFrame extends Frame {

	public TenButtonFrame(String title) {
		super("Ten Buttons Frame");
		this.setLayout(new GridLayout(1, 10));
		
		Button[] btn = new Button[10];
		for(int i=0; i<btn.length; i++) {
			btn[i] = new Button(Integer.toString(i));
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
