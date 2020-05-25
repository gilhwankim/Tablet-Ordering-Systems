package makeSound;

import java.io.FileInputStream;

import javazoom.jl.player.Player;

public class MakeSound {
   
   public static void kitchenOrderSound() {
      try {
         FileInputStream   fis = new FileInputStream("src\\makeSound\\bell.mp3"); //음원 파일 경로
         Player player = new Player(fis);
         player.play(); //플레이어 실행
      }catch (Exception e) {}
   }      
}