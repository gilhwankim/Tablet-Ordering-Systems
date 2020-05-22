package kitchen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KitchenMain extends Application{
	
	public static Stage KitchenStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		KitchenStage = primaryStage;
		Parent parent = FXMLLoader.load(getClass().getResource("OrderBoard.fxml"));
		Scene scene = new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Kitchen");
		primaryStage.toFront();
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
		
	}
}