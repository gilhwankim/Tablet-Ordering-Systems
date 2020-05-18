package makeSound;

public class Test {
		public static void main(String[] args) {
			MakeSound mk = new MakeSound();
			
			mk.start();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}
			
			mk.soundStop(true);			
		}
	}