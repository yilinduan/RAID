import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class RAIDViewApp extends Application {
    Scene scene;
    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        FXMLLoader loader1 = new FXMLLoader(RAIDStarter.class.getResource("RAID.fxml"));
        AnchorPane page1 = loader1.load();
        scene = new Scene(page1);
        stage.setScene(scene);
        stage.show();
    }
}
