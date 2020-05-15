package sec_verify.exam11;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BouncingBall extends Frame {
	
	int BALL_SIZE = 20;
	int FRAME_WIDTH  = 400;
	int FRAME_HEIGHT = 300;
	int TOP;
	int BOTTOM;
	int LEFT;
	int RIGHT;
	int SPEED = 3;
	int x = 100;
	int y = 100;
	int xStep = SPEED;
	int yStep = SPEED;
	
	public BouncingBall(String title) {
		super(title);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		this.setBounds(300,200, FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		setVisible(true);
		Insets insets = getInsets(); // Frame의 테두리의 두께를 얻어온다.
		TOP = insets.top;
		LEFT = insets.left;
		BOTTOM = FRAME_HEIGHT - insets.bottom ;
		RIGHT = FRAME_WIDTH - insets.right;
	}
	
	public void start() {
		while(true) {
			x +=xStep;
			y +=yStep;
			
			//1. x의 값이 왼쪽 테두리(LEFT)보다 작거나 같으면
			//x의 값을 LEFT로 바꾸고 x축 이동방향(xStep)을 반대로 한다.
			if(x <=LEFT) {
				x = LEFT;
				xStep = -xStep;
			}
		
			//2. x의 값이 오른쪽 테두리(RIGHT-BALL_SIZE)보다 크거나 같으면
			//x의 값을 RIGHT-BALL_SIZE로 바꾸고 x축 이동방향(xStep)을 반대로 한다.
			if(x >= RIGHT-BALL_SIZE) {
				x = RIGHT-BALL_SIZE;
				xStep = -xStep;
			}
			
			//3. y의 값이 윗 쪽 테두리(TOP)보다 작거나 같으면
			//y의 값을 TOP으로 바꾸고 y축 이동방향(yStep)을 반대로 한다.
			if(y <= TOP) {
				y = TOP;
				yStep = -yStep;
			}
			
			// 4. y의 값이 아래 쪽 테두리(BOTTON-BALL_SIZE)보다 크거나 같으면
			// y의 값을 BOTTON-BALL_SIZE로 바꾸고 y축 이동방향(yStep)을 반대로 한다.
			if(y >= BOTTOM-BALL_SIZE) {
				y = BOTTOM-BALL_SIZE;
				yStep = -yStep;
			}
			
			repaint();
			
			
			try {
				Thread.sleep(10);
			} catch (Exception e) {}
		}
	} // start()
	
	public void paint(Graphics g) {
		g.setColor(Color.RED);
		g.fillOval(x, y, BALL_SIZE, BALL_SIZE);
	}
}
