package pomodoro.com;

import java.io.IOException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

public class TimerController {

    @FXML
    private Label timerLabel;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button restartButton;
    @FXML
    private ImageView playImage;

    @FXML
    private ImageView pauseImage;

    @FXML
    private ImageView restartImage;

    private Timeline timeline;
    private boolean isPaused = false;
    private boolean isFinished = false;

    private int seg;
    private int minSelect = 25;
    private int focusMinutes = 25;
    private int breakMinutes = 5;

    private timerMode mode;
    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private Pane topPane;
    @FXML
    private BorderPane root;

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

        Rectangle clip = new Rectangle();
        clip.setArcWidth(30);
        clip.setArcHeight(30);

        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());

        root.setClip(clip);
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

    public enum timerMode {
        FOCUS,
        BREAK
    }

    public void startFocus(int min) {
        mode = timerMode.FOCUS;
        minSelect = min;
        focusMinutes = min;
        seg = min * 60;
        updateIcons();
        updateLabel();
        startTimeline();
    }

    public void startBreak(int min) {
        mode = timerMode.BREAK;
        breakMinutes = min;
        seg = min * 60;
        updateIcons();
        updateLabel();
        startTimeline();
    }

    private void startTimeline() {
        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> updateTimer()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateIcons() {
        String folder = (mode == timerMode.BREAK) ? "break" : "focus";

        setImage(playImage, folder + "/play.png");
        setImage(pauseImage, folder + "/pause.png");
        setImage(restartImage, folder + "/restart.png");
    }

    private void finishTimer() {
        timeline.stop();
        timeline = null;
        isFinished = true;
        isPaused = false;

        if (mode == timerMode.BREAK) {
            setImage(playImage, "break/esc.png");
            setImage(pauseImage, "break/time.png");
        } else {
            goToBreak();
        }
    }

    public void setTimerFromOutside(int minutos) {
        if (timeline != null) {
            timeline.stop();
        }

        mode = timerMode.FOCUS;
        minSelect = minutos;
        seg = minutos * 60;
        updateLabel();
    }

    private void setImage(ImageView view, String file) {
        var stream = getClass().getResourceAsStream("/img/" + file);
        view.setImage(new javafx.scene.image.Image(stream));
    }

    public void playTimer() {
        if (mode == timerMode.BREAK && isFinished) {
            goToIndex();
        }
        if (timeline == null) {
            startTimeline();
        } else {
            timeline.play();
        }

        isPaused = false;
        showPauseIcon();

    }

    public void pauseTimer() {
        if (mode == timerMode.BREAK && isFinished) {
            goToFocus();
            return;
        }

        if (isPaused) {
            goToIndex();
            return;
        }

        if (timeline != null) {
            timeline.pause();
        }
        isPaused = true;

        showExitIcon();

    }

    public void restartTimer() {
        if (timeline != null) {
            timeline.stop();
        }

        isFinished = false;
        isPaused = false;

        if (mode == timerMode.FOCUS) {
            seg = minSelect * 60;
        } else {
            seg = breakMinutes * 60;
        }

        showPlayIcon();
        updateLabel();
    }

    private void showPlayIcon() {
        String folder = (mode == timerMode.BREAK) ? "break" : "focus";
        setImage(playImage, folder + "/play.png");
    }

    private void showPauseIcon() {
        String folder = (mode == timerMode.BREAK) ? "break" : "focus";
        setImage(pauseImage, folder + "/pause.png");
    }

    private void showExitIcon() {
        String folder = (mode == timerMode.BREAK) ? "break" : "focus";
        setImage(pauseImage, folder + "/esc.png");
    }

    private void updateTimer() {
        if (seg > 0) {
            seg--;
            updateLabel();
        } else {
            finishTimer();
        }
    }

    private void updateLabel() {
        int min = seg / 60;
        int segs = seg % 60;

        timerLabel.setText(String.format("%02d:%02d", min, segs));
    }

    private void goToBreak() {
        loadScene("/pomodoro/com/pausa.fxml", breakMinutes, timerMode.BREAK);

    }

    private void goToFocus() {
        loadScene("/pomodoro/com/timer.fxml", minSelect, timerMode.FOCUS);
    }

    private void goToIndex() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pomodoro/com/index.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) timerLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadScene(String fxml, int minutos, timerMode nextMode) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            TimerController controller = loader.getController();

            if (nextMode == timerMode.BREAK) {
                controller.startBreak(minutos);
            } else {
                controller.startFocus(minutos);
            }

            Stage stage = (Stage) timerLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
