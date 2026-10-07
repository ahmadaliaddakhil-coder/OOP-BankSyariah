package ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BankSyariahApp extends Application {
    @Override
    public void start(Stage stage) {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/views/bank-syariah.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException error) {
            throw new IllegalStateException("Tidak dapat memuat tampilan utama JavaFX.", error);
        }

        Scene scene = new Scene(root, 1240, 780);
        scene.getStylesheets().add(
                getClass().getResource("/css/app.css").toExternalForm());
        stage.setTitle("Berkah | Pembiayaan Modal Syariah");
        stage.setMinWidth(1100);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
