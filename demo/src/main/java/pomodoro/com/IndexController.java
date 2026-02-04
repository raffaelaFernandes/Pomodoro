package pomodoro.com;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class IndexController {
 private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Pane topPane;

    @FXML
    public void initialize() {
        topPane.setOnMousePressed(event -> {
            Stage stage = (Stage) topPane.getScene().getWindow();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        topPane.setOnMouseDragged(event -> {
            Stage stage = (Stage) topPane.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    @FXML
    private void close(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource())
                .getScene()
                .getWindow();
        stage.close();
    }

    @FXML
    private void minimize(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource())
                .getScene()
                .getWindow();
        stage.setIconified(true);
    }

    private void loadTimer(ActionEvent event, int minutos) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/pomodoro/com/timer.fxml"));

        Parent root = loader.load();

        TimerController controller = loader.getController();
        controller.setTimerFromOutside(minutos);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);

        // DEFINA A TRANSPARÊNCIA AQUI
        scene.setFill(Color.TRANSPARENT);

        stage.setScene(scene);
        stage.show();
    }

    public void switchToTimer25(ActionEvent event) throws IOException {
        loadTimer(event, 25);
    }

    public void switchToTimer30(ActionEvent event) throws IOException {
        loadTimer(event, 30);
    }

    public void switchToTimer60(ActionEvent event) throws IOException {
        loadTimer(event, 60);
    }

}
