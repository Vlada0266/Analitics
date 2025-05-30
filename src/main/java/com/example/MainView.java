package com.example;

import com.example.population.PopulationView;
import com.example.migration.MigrationView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainView {

    public static void launch(Stage primaryStage) {
        Stage choiceStage = new Stage();
        choiceStage.setTitle("Выбор варианта");

        Label promptLabel = new Label("Выберите тип анализа:");

        Button populationBtn = new Button("Численность населения");
        Button migrationBtn = new Button("Миграция населения");

        populationBtn.setOnAction(e -> {
            choiceStage.hide();
            PopulationView populationView = new PopulationView();
            populationView.start(primaryStage);
            primaryStage.setOnHidden(ev -> choiceStage.show());
        });

        migrationBtn.setOnAction(e -> {
            choiceStage.hide();
            MigrationView migrationView = new MigrationView();
            migrationView.start(primaryStage);
            primaryStage.setOnHidden(ev -> choiceStage.show());
        });

        // Закрытие окна выбора — завершает приложение
        choiceStage.setOnCloseRequest((WindowEvent event) -> {
            System.exit(0);
        });

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(promptLabel, populationBtn, migrationBtn);

        Scene scene = new Scene(layout, 300, 180);
        choiceStage.setScene(scene);
        choiceStage.show();
    }
}
