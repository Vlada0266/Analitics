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

    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Анализ численности населения");

        // Метка с инструкцией
        Label fileLabel = new Label("Выберите CSV-файл с данными:");

        // Кнопка загрузки файла
        Button loadButton = new Button("Загрузить данные");

        // Таблица
        tableView = new TableView<>();
        TableColumn<PopulationData, Integer> yearCol = new TableColumn<>("Год");
        yearCol.setCellValueFactory(data -> data.getValue().yearProperty().asObject());
        TableColumn<PopulationData, Double> valueCol = new TableColumn<>("Численность (млн)");
        valueCol.setCellValueFactory(data -> data.getValue().valueProperty().asObject());
        tableView.getColumns().addAll(yearCol, valueCol);
        tableView.setPrefHeight(200);

        // Оси графика
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Год");
        xAxis.setForceZeroInRange(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Численность (млн)");

        // График
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("График численности населения");
        chart.setCreateSymbols(false);
        chart.setAnimated(false);
        chart.setLegendVisible(true);
        chart.setPrefHeight(400);

        // Метки для прироста и убыли
        growthLabel = new Label("Макс. прирост: -");
        declineLabel = new Label("Макс. убыль: -");
        HBox statsBox = new HBox(20, growthLabel, declineLabel);
        statsBox.setPadding(new Insets(10));

        // Обработчик кнопки загрузки
        loadButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите CSV-файл");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файлы", "*.csv"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                List<PopulationData> data = loadDataFromFile(file);
                if (data != null) {
                    displayData(data);
                    double maxGrowth = calculateMaxGrowth(data);
                    double maxDecline = calculateMaxDecline(data);
                    setGrowthAndDecline(maxGrowth, maxDecline);
                } else {
                    showAlert("Ошибка", "Не удалось загрузить данные из файла.");
                }
            }
        });

        VBox root = new VBox(10, fileLabel, loadButton, tableView, statsBox, chart);
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

    public void displayData(List<PopulationData> populationData) {
        tableView.getItems().clear();
        tableView.getItems().addAll(populationData);

        chart.getData().clear();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Численность населения");
        for (PopulationData data : populationData) {
            series.getData().add(new XYChart.Data<>(data.getYear(), data.getValue()));
        }
        chart.getData().add(series);
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
