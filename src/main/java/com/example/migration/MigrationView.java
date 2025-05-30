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
    private final MigrationController controller = new MigrationController();
    private Stage primaryStage;
    private TableView<MigrationData> tableView;
    private LineChart<Number, Number> chart;
    private Label migrationChangeLabel;

    public void start(Stage stage) {
        this.primaryStage = stage;
        primaryStage.setTitle("Анализ миграции населения");

        // Верхняя панель: выбор файла и ввод прогноза
        Label fileLabel = new Label("Выберите CSV-файл с данными:");
        Button loadButton = new Button("Загрузить данные");
        Label forecastLabel = new Label("Количество лет прогноза:");
        TextField forecastInput = new TextField("5");
        forecastInput.setPrefWidth(60);
        HBox topBox = new HBox(10, fileLabel, loadButton, forecastLabel, forecastInput);
        topBox.setPadding(new Insets(10));

        // Таблица
        tableView = new TableView<>();
        TableColumn<MigrationData, Number> yearCol = new TableColumn<>("Год");
        yearCol.setCellValueFactory(r -> r.getValue().yearProperty());
        TableColumn<MigrationData, Number> immCol = new TableColumn<>("Иммигранты (тыс.)");
        immCol.setCellValueFactory(r -> r.getValue().immigrantsProperty());
        TableColumn<MigrationData, Number> emCol = new TableColumn<>("Эмигранты (тыс.)");
        emCol.setCellValueFactory(r -> r.getValue().emigrantsProperty());
        tableView.getColumns().addAll(yearCol, immCol, emCol);
        tableView.setPrefHeight(200);

        // Статистика
        migrationChangeLabel = new Label("Макс. % изменения миграции: -");
        HBox statsBox = new HBox(10, migrationChangeLabel);
        statsBox.setPadding(new Insets(10));

        // График
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Год");
        xAxis.setForceZeroInRange(false);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Численность (тыс.)");
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Иммиграция и эмиграция");
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.setPrefHeight(400);

        // Сборка корневого контейнера
        VBox root = new VBox(10, topBox, tableView, statsBox, chart);
        root.setPadding(new Insets(10));
        Scene scene = new Scene(root, 900, 750);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Обработчик загрузки
        loadButton.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Выберите CSV-файл");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV файлы", "*.csv"));
            File file = chooser.showOpenDialog(primaryStage);
            if (file == null) return;

            List<MigrationData> data = controller.loadMigrationData(file);
            if (data == null || data.isEmpty()) {
                showAlert("Ошибка", "Не удалось загрузить данные из файла.");
                return;
            }

            int forecastYears;
            try {
                forecastYears = Integer.parseInt(forecastInput.getText().trim());
                if (forecastYears < 0) throw new NumberFormatException();
            } catch (Exception ex) {
                showAlert("Ошибка", "Введите корректное количество лет прогноза.");
                forecastYears = 5;
            }

            updateView(data, forecastYears);
        });
    }

    private void updateView(List<MigrationData> data, int forecastYears) {
        // Заполняем таблицу
        tableView.getItems().setAll(data);

        // Берём статистику
        double maxPct = controller.getMaxMigrationChangePercent(data);
        migrationChangeLabel.setText(
                String.format("Макс. %% изменения миграции за год: %.2f%%", maxPct)
        );

        // Очищаем график и рисуем серии
        chart.getData().clear();

        // Фактические данные
        XYChart.Series<Number, Number> seriesImm = new XYChart.Series<>();
        seriesImm.setName("Иммигранты");
        XYChart.Series<Number, Number> seriesEm  = new XYChart.Series<>();
        seriesEm.setName("Эмигранты");

        for (MigrationData d : data) {
            seriesImm.getData().add(new XYChart.Data<>(d.getYear(), d.getImmigrants()));
            seriesEm.getData().add(new XYChart.Data<>(d.getYear(), d.getEmigrants()));
        }
        chart.getData().addAll(seriesImm, seriesEm);

        // Прогноз
        List<Double> immForecast = controller.getImmigrationForecast(data, 3, forecastYears);
        List<Double> emForecast  = controller.getEmigrationForecast(data, 3, forecastYears);
        int lastYear = data.get(data.size() - 1).getYear();

        if (!immForecast.isEmpty()) {
            XYChart.Series<Number, Number> fImm = new XYChart.Series<>();
            fImm.setName("Прогноз (иммигранты)");
            for (int i = 0; i < immForecast.size(); i++) {
                fImm.getData().add(new XYChart.Data<>(lastYear + i + 1, immForecast.get(i)));
            }
            chart.getData().add(fImm);
        }

        if (!emForecast.isEmpty()) {
            XYChart.Series<Number, Number> fEm = new XYChart.Series<>();
            fEm.setName("Прогноз (эмигранты)");
            for (int i = 0; i < emForecast.size(); i++) {
                fEm.getData().add(new XYChart.Data<>(lastYear + i + 1, emForecast.get(i)));
            }
            chart.getData().add(fEm);
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
