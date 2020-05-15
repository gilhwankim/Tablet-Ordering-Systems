package tablet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TabletMain extends Application{
	
	public static Stage clientStage;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		clientStage = primaryStage;
		Parent parent = FXMLLoader.load(getClass().getResource("tablet.fxml"));
		Scene scene = new Scene(parent);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Table");
		primaryStage.toFront();
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}

}
