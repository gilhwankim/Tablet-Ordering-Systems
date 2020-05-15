package sec_verify.exam02;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@SuppressWarnings("serial")
public class MyBorderLayoutFrame extends Frame {

	public MyBorderLayoutFrame(String title) {
		super("BorderLayout Practice");
		this.setLayout(new BorderLayout(10, 10));
		this.add(new Button("East"), BorderLayout.EAST);
		this.add(new Button("West"), BorderLayout.WEST);
		this.add(new Button("North"), BorderLayout.NORTH);
		this.add(new Button("South"), BorderLayout.SOUTH);
		this.add(new Button("Center"), BorderLayout.CENTER);
		
		this.setSize(400,200);
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
