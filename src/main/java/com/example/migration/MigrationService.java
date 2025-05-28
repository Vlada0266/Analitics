package com.example.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MigrationService {

    // загрузка из CSV
    public List<MigrationData> loadDataFromCSV(File file) {
        List<MigrationData> dataList = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] parts = line.split("[,;\\t]");
                if (parts.length >= 2) {
                    int year = Integer.parseInt(parts[0].trim());
                    double value = Double.parseDouble(parts[1].trim());
                    dataList.add(new MigrationData(year, value));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    // Анализ статистики
    public MigrationStats calculateMigrationStats(List<MigrationData> data) {
        if (data == null || data.size() < 2) return null;

        double maxIncrease = Double.NEGATIVE_INFINITY;
        double maxDecrease = Double.POSITIVE_INFINITY;

        for (int i = 1; i < data.size(); i++) {
            double prev = data.get(i - 1).getValue();
            double curr = data.get(i).getValue();
            double changePercent = ((curr - prev) / Math.abs(prev)) * 100;

            if (changePercent > maxIncrease) maxIncrease = changePercent;
            if (changePercent < maxDecrease) maxDecrease = changePercent;
        }

        return new MigrationStats(maxIncrease, maxDecrease);
    }

    // Класс для хранения статистики
    public static class MigrationStats {
        private final double maxIncreasePercent;
        private final double maxDecreasePercent;

        public MigrationStats(double maxIncreasePercent, double maxDecreasePercent) {
            this.maxIncreasePercent = maxIncreasePercent;
            this.maxDecreasePercent = maxDecreasePercent;
        }

        public double getMaxIncreasePercent() { return maxIncreasePercent; }
        public double getMaxDecreasePercent() { return maxDecreasePercent; }
    }

    // Прогноз скользящим средним
    public List<Double> calculateForecast(List<MigrationData> data, int windowSize, int forecastYears) {
        List<Double> forecast = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (MigrationData d : data) {
            values.add(d.getValue());
        }

        for (int i = 0; i < forecastYears; i++) {
            int startIdx = values.size() - windowSize;
            double sum = 0;
            for (int j = startIdx; j < values.size(); j++) {
                sum += values.get(j);
            }
            double avg = sum / windowSize;
            forecast.add(avg);
            values.add(avg);
        }
        return forecast;
    }
}
