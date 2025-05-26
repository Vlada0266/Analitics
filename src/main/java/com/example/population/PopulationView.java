package com.example.population;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class PopulationView {
    private final PopulationController controller;
    private List<PopulationData> populationData;

    private Label maxIncreaseLabel;
    private Label maxDecreaseLabel;

    public PopulationView(Stage primaryStage) {
        this.controller = new PopulationController();

        Label fileLabel = new Label("Выберите CSV-файл с данными (год;численность):");
        Button loadButton = new Button("Загрузить данные");

        maxIncreaseLabel = new Label("Максимальный прирост за год: — данные не загружены —");
        maxDecreaseLabel = new Label("Максимальное снижение за год: — данные не загружены —");

        Label windowLabel = new Label("Окно скользящей средней:");
        TextField windowField = new TextField("3");
        Label forecastLabel = new Label("Число лет для прогноза:");
        TextField forecastField = new TextField("5");
        Button forecastButton = new Button("Рассчитать прогноз");

        LineChart<Number, Number> chart = new LineChart<>(
                new NumberAxis(), new NumberAxis()
        );
        chart.setTitle("Анализ численности населения");
        chart.setCreateSymbols(false);

        VBox root = new VBox(10, fileLabel, loadButton,
                maxIncreaseLabel, maxDecreaseLabel,
                windowLabel, windowField,
                forecastLabel, forecastField,
                forecastButton, chart);
        root.setPadding(new Insets(15));

        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите CSV-файл");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                populationData = controller.loadPopulationData(file);
                if (populationData.isEmpty()) {
                    showAlert("Ошибка", "Не удалось загрузить данные.");
                } else {
                    showAlert("Успех", "Данные успешно загружены.");
                    displayChart(chart);
                    displayGrowthStats(populationData);
                }
            }
        });

        forecastButton.setOnAction(e -> {
            if (populationData == null || populationData.isEmpty()) {
                showAlert("Ошибка", "Сначала загрузите данные.");
                return;
            }
            try {
                int window = Integer.parseInt(windowField.getText());
                int years = Integer.parseInt(forecastField.getText());
                List<Double> forecast = controller.forecast(populationData, window, years);
                displayForecast(chart, forecast);
            } catch (NumberFormatException ex) {
                showAlert("Ошибка", "Введите корректные числовые значения.");
            }
        });

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Прогноз численности населения");
        primaryStage.show();
    }

    private void displayChart(LineChart<Number, Number> chart) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Исторические данные");
        for (PopulationData data : populationData) {
            series.getData().add(new XYChart.Data<>(data.getYear(), data.getPopulation()));
        }
        chart.getData().clear();
        chart.getData().add(series);
    }

    private void displayForecast(LineChart<Number, Number> chart, List<Double> forecast) {
        if (populationData == null || populationData.isEmpty()) return;

        int lastYear = populationData.get(populationData.size() - 1).getYear();
        XYChart.Series<Number, Number> forecastSeries = new XYChart.Series<>();
        forecastSeries.setName("Прогноз");

        for (int i = 0; i < forecast.size(); i++) {
            forecastSeries.getData().add(new XYChart.Data<>(lastYear + i + 1, forecast.get(i)));
        }

        chart.getData().add(forecastSeries);
    }

    private void displayGrowthStats(List<PopulationData> data) {
        PopulationService.GrowthStats stats = controller.calculateGrowthStats(data);
        if (stats != null) {
            maxIncreaseLabel.setText(String.format("Максимальный прирост за год: %.2f%%", stats.getMaxIncreasePercent()));
            maxDecreaseLabel.setText(String.format("Максимальное снижение за год: %.2f%%", stats.getMaxDecreasePercent()));
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
