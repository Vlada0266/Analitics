package com.example.migration;

import java.io.File;
import java.util.List;

public class MigrationController {
    private final MigrationService migrationService;

    public MigrationController() {
        this.migrationService = new MigrationService();
    }

    // В контроллере - интерфейс загрузки данных из файла (передаем файл, получаем список данных)
    public List<MigrationData> loadMigrationData(File file) {
        return migrationService.loadDataFromCSV(file);
    }

    // Запрос прогнозных данных
    public List<Double> getForecast(List<MigrationData> data, int windowSize, int forecastYears) {
        return migrationService.calculateForecast(data, windowSize, forecastYears);
    }

    // Получение статистики
    public MigrationService.MigrationStats getMigrationStats(List<MigrationData> data) {
        return migrationService.calculateMigrationStats(data);
    }
}
