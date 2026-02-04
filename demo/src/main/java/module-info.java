module pomodoro.com {
    requires javafx.controls;
    requires javafx.fxml;

    opens pomodoro.com to javafx.fxml;
    exports pomodoro.com;
}
