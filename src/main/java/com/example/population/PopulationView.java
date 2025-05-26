package com.example.population;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class PopulationView {
    private Stage primaryStage;
    private TableView<PopulationData> tableView;
    private LineChart<Number, Number> chart;
    private Label growthLabel;
    private Label declineLabel;
    private TextField forecastInput;

    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Анализ численности населения");

        Label fileLabel = new Label("Выберите CSV-файл с данными:");

        Button loadButton = new Button("Загрузить данные");

        // Поле для ввода количества лет прогноза
        Label forecastLabel = new Label("Количество лет прогноза:");
        TextField forecastYearsInput = new TextField("5");
        forecastYearsInput.setPrefWidth(60);
        HBox forecastBox = new HBox(10, forecastLabel, forecastYearsInput);
        forecastBox.setPadding(new Insets(5));

        // Таблица
        tableView = new TableView<>();
        TableColumn<PopulationData, Integer> yearCol = new TableColumn<>("Год");
        yearCol.setCellValueFactory(data -> data.getValue().yearProperty().asObject());
        TableColumn<PopulationData, Double> valueCol = new TableColumn<>("Численность (млн)");
        valueCol.setCellValueFactory(data -> data.getValue().valueProperty().asObject());
        tableView.getColumns().addAll(yearCol, valueCol);
        tableView.setPrefHeight(200);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Год");
        xAxis.setForceZeroInRange(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Численность (млн)");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("График численности населения");
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
                List<PopulationData> data = loadDataFromFile(file);
                if (data != null) {
                    int forecastYears = 5; // по умолчанию
                    try {
                        String inputText = forecastYearsInput.getText().trim();
                        if (!inputText.isEmpty()) {
                            forecastYears = Integer.parseInt(inputText);
                            if (forecastYears < 0) throw new NumberFormatException();
                        }
                    } catch (NumberFormatException ex) {
                        showAlert("Ошибка", "Введите корректное количество лет прогноза (целое положительное число). Используется значение по умолчанию (5).");
                    }

                    displayData(data, forecastYears);
                    double maxGrowth = calculateMaxGrowth(data);
                    double maxDecline = calculateMaxDecline(data);
                    setGrowthAndDecline(maxGrowth, maxDecline);
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


    // Метод загрузки данных из файла с разделителем ";"
    private List<PopulationData> loadDataFromFile(File file) {
        List<PopulationData> list = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    int year = Integer.parseInt(parts[0].trim());
                    double value = Double.parseDouble(parts[1].trim());
                    list.add(new PopulationData(year, value));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return list;
    }

    private double calculateMaxGrowth(List<PopulationData> data) {
        double maxGrowth = 0;
        for (int i = 1; i < data.size(); i++) {
            double prev = data.get(i - 1).getValue();
            double curr = data.get(i).getValue();
            double growth = ((curr - prev) / prev) * 100;
            if (growth > maxGrowth) maxGrowth = growth;
        }
        return maxGrowth;
    }

    private double calculateMaxDecline(List<PopulationData> data) {
        double maxDecline = 0;
        for (int i = 1; i < data.size(); i++) {
            double prev = data.get(i - 1).getValue();
            double curr = data.get(i).getValue();
            double decline = ((prev - curr) / prev) * 100;
            if (decline > maxDecline) maxDecline = decline;
        }
        return maxDecline;
    }

    private List<PopulationData> calculateMovingAverageForecast(List<PopulationData> data, int windowSize, int forecastYears) {
        List<PopulationData> forecast = new ArrayList<>();
        int dataSize = data.size();
        if (dataSize < windowSize) return forecast; // Если данных мало — прогноз не считаем

        for (int i = 0; i < forecastYears; i++) {
            double sum = 0;
            for (int j = dataSize - windowSize + i; j < dataSize + i; j++) {
                double val;
                if (j < dataSize) {
                    val = data.get(j).getValue();
                } else {
                    val = forecast.get(j - dataSize).getValue();
                }
                sum += val;
            }
            double avg = sum / windowSize;
            int year = data.get(dataSize - 1).getYear() + i + 1;
            forecast.add(new PopulationData(year, avg));
        }
        return forecast;
    }

    public void displayData(List<PopulationData> populationData, int forecastYears) {
        tableView.getItems().clear();
        tableView.getItems().addAll(populationData);

        chart.getData().clear();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Численность населения");
        for (PopulationData data : populationData) {
            series.getData().add(new XYChart.Data<>(data.getYear(), data.getValue()));
        }
        chart.getData().add(series);

        int windowSize = 3; // можно сделать вводимым тоже, но пока фиксировано

        List<PopulationData> forecast = calculateMovingAverageForecast(populationData, windowSize, forecastYears);

        if (!forecast.isEmpty()) {
            XYChart.Series<Number, Number> forecastSeries = new XYChart.Series<>();
            forecastSeries.setName("Прогноз (скользящая средняя)");
            for (PopulationData data : forecast) {
                forecastSeries.getData().add(new XYChart.Data<>(data.getYear(), data.getValue()));
            }
            chart.getData().add(forecastSeries);

            // Пунктирная красная линия
            forecastSeries.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-stroke: red; -fx-stroke-dash-array: 12 6;");
                }
            });
        }
    }

    public void setGrowthAndDecline(double maxGrowth, double maxDecline) {
        growthLabel.setText(String.format("Макс. прирост: %.2f%%", maxGrowth));
        declineLabel.setText(String.format("Макс. убыль: %.2f%%", maxDecline));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
