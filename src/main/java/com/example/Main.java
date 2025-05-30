package com.example;

import com.example.migration.MigrationView;
import com.example.population.PopulationView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Первое окно с PopulationView
        PopulationView populationView = new PopulationView();
        populationView.start(primaryStage);

        // Второе окно с MigrationView
        Stage migrationStage = new Stage();
        MigrationView migrationView = new MigrationView();
        migrationView.start(migrationStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

