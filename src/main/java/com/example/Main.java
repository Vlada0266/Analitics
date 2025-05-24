package com.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        Tab populationTab = new Tab("Численность населения");
        populationTab.setContent(createPopulationTabContent());
        populationTab.setClosable(false);

        Tab migrationTab = new Tab("Миграция населения");
        migrationTab.setContent(createMigrationTabContent());
        migrationTab.setClosable(false);

        tabPane.getTabs().addAll(populationTab, migrationTab);

        Scene scene = new Scene(tabPane, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Анализ населения России");
        primaryStage.show();
    }

    private VBox createPopulationTabContent() {
        Button loadFileBtn = new Button("Загрузить файл");
        TableView<?> table = new TableView<>();
        LineChart<Number, Number> chart = createLineChart("Год", "Численность");
        TextField nField = new TextField();
        nField.setPromptText("Длина окна n");
        TextField NField = new TextField();
        NField.setPromptText("Количество лет прогноза N");
        Button forecastBtn = new Button("Построить прогноз");

        HBox inputBox = new HBox(10, nField, NField, forecastBtn);
        VBox vbox = new VBox(10, loadFileBtn, table, chart, inputBox);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    private VBox createMigrationTabContent() {
        Button loadFileBtn = new Button("Загрузить файл");
        TableView<?> table = new TableView<>();
        LineChart<Number, Number> chart = createLineChart("Год", "Миграция");
        TextField nField = new TextField();
        nField.setPromptText("Длина окна n");
        TextField NField = new TextField();
        NField.setPromptText("Количество лет прогноза N");
        Button forecastBtn = new Button("Построить прогноз");

        HBox inputBox = new HBox(10, nField, NField, forecastBtn);
        VBox vbox = new VBox(10, loadFileBtn, table, chart, inputBox);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    private LineChart<Number, Number> createLineChart(String xLabel, String yLabel) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        return new LineChart<>(xAxis, yAxis);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
