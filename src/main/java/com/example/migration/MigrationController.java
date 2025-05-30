package com.example.migration;

import java.io.File;
import java.util.List;

public class MigrationController {
    private final MigrationService service = new MigrationService();

    public List<MigrationData> loadMigrationData(File f) {
        return service.loadDataFromCSV(f);
    }

    public double getMaxMigrationChangePercent(List<MigrationData> data) {
        return service.calculateMaxMigrationChangePercent(data);
    }

    public List<Double> getImmigrationForecast(List<MigrationData> data,
                                               int windowSize, int forecastYears) {
        return service.calculateImmigrationForecast(data, windowSize, forecastYears);
    }

    public List<Double> getEmigrationForecast(List<MigrationData> data,
                                              int windowSize, int forecastYears) {
        return service.calculateEmigrationForecast(data, windowSize, forecastYears);
    }
}
