package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	
	private SampleController controller;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader=new FXMLLoader(getClass().getResource("Sample.fxml"));
			BorderPane root = (BorderPane)loader.load();
			primaryStage.setTitle("AIMPClone v1.0");
			controller=loader.getController();
			Scene scene = new Scene(root);
			controller.setStage(primaryStage);
			primaryStage.getIcons().add(new Image("file:G:/java-workspace/MPproject/icon.png"));
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			controller.shiftingTextInit();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
