package com.example.population;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PopulationService {

    public List<PopulationData> loadDataFromCSV(File file) {
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

    public GrowthStats calculateGrowthStats(List<PopulationData> data) {
        double maxGrowth = 0;
        double maxDecline = 0;
        for (int i = 1; i < data.size(); i++) {
            double prev = data.get(i - 1).getValue();
            double curr = data.get(i).getValue();
            double growth = ((curr - prev) / prev) * 100;
            if (growth > maxGrowth) maxGrowth = growth;

            double decline = ((prev - curr) / prev) * 100;
            if (decline > maxDecline) maxDecline = decline;
        }
        return new GrowthStats(maxGrowth, maxDecline);
    }

    public List<PopulationData> calculateMovingAverageForecast(List<PopulationData> data, int windowSize, int forecastYears) {
        List<PopulationData> forecast = new ArrayList<>();
        int dataSize = data.size();
        if (dataSize < windowSize) return forecast;

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

    public static class GrowthStats {
        public final double maxGrowth;
        public final double maxDecline;

        public GrowthStats(double maxGrowth, double maxDecline) {
            this.maxGrowth = maxGrowth;
            this.maxDecline = maxDecline;
        }
    }
}
