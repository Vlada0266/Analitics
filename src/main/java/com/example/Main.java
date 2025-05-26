package com.example;

import com.example.population.PopulationView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        new PopulationView(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
