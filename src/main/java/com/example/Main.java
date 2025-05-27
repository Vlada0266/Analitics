package com.example;

import com.example.migration.MigrationView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MigrationView view = new MigrationView();
        view.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args); // Запуск JavaFX
    }
}
