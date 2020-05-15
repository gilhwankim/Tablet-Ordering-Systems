package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ServerMain extends Application{
	
	public static Stage serverStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		serverStage = primaryStage;
		Parent parent = FXMLLoader.load(getClass().getResource("server.fxml"));
		Scene scene = new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.setTitle("POS");
		primaryStage.toFront();
		primaryStage.centerOnScreen();
		primaryStage.show();
		System.out.println("Â¥ÀÜ!");
	}
	public static void main(String[] args) {
		launch(args);
	}

}
