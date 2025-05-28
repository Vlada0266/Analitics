package com.example.migration;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class MigrationView {
    private final MigrationController controller;
    private Stage primaryStage;
    private TableView<MigrationData> tableView;
    private LineChart<Number, Number> chart;
    private Label growthLabel;
    private Label declineLabel;

    public MigrationView() {
        this.controller = new MigrationController();
    }

    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Анализ миграции населения");

        Label fileLabel = new Label("Выберите CSV-файл с данными:");

        Button loadButton = new Button("Загрузить данные");

        Label forecastLabel = new Label("Количество лет прогноза:");
        TextField forecastInput = new TextField("5");
        forecastInput.setPrefWidth(60);
        HBox forecastBox = new HBox(10, forecastLabel, forecastInput);
        forecastBox.setPadding(new Insets(5));

        // Таблица
        tableView = new TableView<>();
        TableColumn<MigrationData, Integer> yearCol = new TableColumn<>("Год");
        yearCol.setCellValueFactory(data -> data.getValue().yearProperty().asObject());
        TableColumn<MigrationData, Double> valueCol = new TableColumn<>("Миграционный баланс (тыс.)");
        valueCol.setCellValueFactory(data -> data.getValue().valueProperty().asObject());
        tableView.getColumns().addAll(yearCol, valueCol);
        tableView.setPrefHeight(200);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Год");
        xAxis.setForceZeroInRange(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Миграционный баланс (тыс.)");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("График миграционного баланса");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);
        chart.setLegendVisible(true);
        chart.setPrefHeight(400);

        growthLabel = new Label("Макс. прирост: -");
        declineLabel = new Label("Макс. убыль: -");
        HBox statsBox = new HBox(20, growthLabel, declineLabel);
        statsBox.setPadding(new Insets(10));

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите CSV-файл");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файлы", "*.csv"));
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null) {
                List<MigrationData> data = controller.loadMigrationData(file);

                if (data != null && !data.isEmpty()) {
                    int forecastYears = 5;
                    try {
                        String text = forecastInput.getText().trim();
                        if (!text.isEmpty()) {
                            forecastYears = Integer.parseInt(text);
                            if (forecastYears < 0) throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex) {
                        showAlert("Ошибка", "Введите корректное количество лет прогноза. Используется значение 5.");
                    }

                    updateView(data, forecastYears);
                } else {
                    showAlert("Ошибка", "Не удалось загрузить данные из файла.");
                }
            }
        });

        VBox root = new VBox(10, fileLabel, forecastBox, loadButton, tableView, statsBox, chart);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 800, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateView(List<MigrationData> data, int forecastYears) {
        // Обновляем таблицу
        tableView.getItems().clear();
        tableView.getItems().addAll(data);

        // Обновляем график
        chart.getData().clear();

        XYChart.Series<Number, Number> actualSeries = new XYChart.Series<>();
        actualSeries.setName("Миграционный баланс");
        for (MigrationData d : data) {
            actualSeries.getData().add(new XYChart.Data<>(d.getYear(), d.getValue()));
        }
        chart.getData().add(actualSeries);

        // Получаем прогноз из контроллера
        List<Double> forecastValues = controller.getForecast(data, 3, forecastYears);

        if (!forecastValues.isEmpty()) {
            XYChart.Series<Number, Number> forecastSeries = new XYChart.Series<>();
            forecastSeries.setName("Прогноз (скользящая средняя)");

            int lastYear = data.get(data.size() - 1).getYear();
            for (int i = 0; i < forecastValues.size(); i++) {
                forecastSeries.getData().add(new XYChart.Data<>(lastYear + i + 1, forecastValues.get(i)));
            }
            chart.getData().add(forecastSeries);
        }

        // Обновляем статистику
        MigrationService.MigrationStats stats = controller.getMigrationStats(data);
        if (stats != null) {
            growthLabel.setText(String.format("Макс. прирост: %.2f%%", stats.getMaxIncreasePercent()));
            declineLabel.setText(String.format("Макс. убыль: %.2f%%", stats.getMaxDecreasePercent()));
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
