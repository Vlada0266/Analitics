package com.example.population;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PopulationService {

    public List<PopulationData> loadDataFromCSV(File file) {
        List<PopulationData> dataList = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] parts = line.split("[,;\\t]");
                if (parts.length >= 2) {
                    int year = Integer.parseInt(parts[0].trim());
                    double population = Double.parseDouble(parts[1].trim());
                    dataList.add(new PopulationData(year, population));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public List<Double> calculateForecast(List<PopulationData> data, int windowSize, int forecastYears) {
        List<Double> forecast = new ArrayList<>();
        List<Double> values = data.stream().map(PopulationData::getValue).toList();

        for (int i = 0; i < forecastYears; i++) {
            int startIdx = values.size() - windowSize;
            double sum = 0;
            for (int j = startIdx; j < values.size(); j++) {
                sum += values.get(j);
            }
            double avg = sum / windowSize;
            forecast.add(avg);
            values = new ArrayList<>(values); // копия, чтобы не менять исходник
            values.add(avg);
        }
        return forecast;
    }
    public GrowthStats calculateGrowthStats(List<PopulationData> data) {
        if (data == null || data.size() < 2) return null;

        double maxIncrease = Double.NEGATIVE_INFINITY;
        double maxDecrease = Double.POSITIVE_INFINITY;

        for (int i = 1; i < data.size(); i++) {
            double prev = data.get(i - 1).getValue();
            double curr = data.get(i).getValue();
            double changePercent = ((curr - prev) / prev) * 100;

            if (changePercent > maxIncrease) {
                maxIncrease = changePercent;
            }
            if (changePercent < maxDecrease) {
                maxDecrease = changePercent;
            }
        }

        return new GrowthStats(maxIncrease, maxDecrease);
    }

    public static class GrowthStats {
        private final double maxIncreasePercent;
        private final double maxDecreasePercent;

        public GrowthStats(double maxIncreasePercent, double maxDecreasePercent) {
            this.maxIncreasePercent = maxIncreasePercent;
            this.maxDecreasePercent = maxDecreasePercent;
        }

        public double getMaxIncreasePercent() {
            return maxIncreasePercent;
        }

        public double getMaxDecreasePercent() {
            return maxDecreasePercent;
        }
    }
}
