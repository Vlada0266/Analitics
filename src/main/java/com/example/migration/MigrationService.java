package com.example.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

    public class MigrationService {

        // Загружает (год;imm;em)
        public List<MigrationData> loadDataFromCSV(File file) {
            List<MigrationData> data = new ArrayList<>();
            try {
                for (String line : Files.readAllLines(file.toPath())) {
                    String[] p = line.split("[,;\\t]");
                    if (p.length >= 3) {
                        int year = Integer.parseInt(p[0].trim());
                        double imm = Double.parseDouble(p[1].trim());
                        double em  = Double.parseDouble(p[2].trim());
                        data.add(new MigrationData(year, imm, em));
                    }
                }
            } catch (IOException|NumberFormatException e) {
                e.printStackTrace();
            }
            return data;
        }

        // 1) Макс % изменения общего потока (imm+em)
        public double calculateMaxMigrationChangePercent(List<MigrationData> data) {
            if (data==null||data.size()<2) return 0;
            double max=0;
            for(int i=1;i<data.size();i++){
                double prev=data.get(i-1).getImmigrants()+data.get(i-1).getEmigrants();
                double cur =data.get(i).getImmigrants() +data.get(i).getEmigrants();
                if(prev==0) continue;
                double pct = Math.abs((cur-prev)/prev)*100;
                if(pct>max) max=pct;
            }
            return max;
        }

        // 2) Прогноз по иммигрантам
        public List<Double> calculateImmigrationForecast(List<MigrationData> data,
                                                         int windowSize, int forecastYears) {
            List<Double> vals = new ArrayList<>();
            for(var d:data) vals.add(d.getImmigrants());
            return slidingAverageForecast(vals, windowSize, forecastYears);
        }

        // 3) Прогноз по эмигрантам
        public List<Double> calculateEmigrationForecast(List<MigrationData> data,
                                                        int windowSize, int forecastYears) {
            List<Double> vals = new ArrayList<>();
            for(var d:data) vals.add(d.getEmigrants());
            return slidingAverageForecast(vals, windowSize, forecastYears);
        }

        // Вспомогательный метод
        private List<Double> slidingAverageForecast(List<Double> values,
                                                    int windowSize, int forecastYears) {
            List<Double> forecast = new ArrayList<>();
            List<Double> buf = new ArrayList<>(values);
            for(int i=0;i<forecastYears;i++){
                int start = buf.size()-windowSize;
                double sum=0;
                for(int j=start;j<buf.size();j++) sum+=buf.get(j);
                double avg = sum/windowSize;
                forecast.add(avg);
                buf.add(avg);
            }
            return forecast;
        }
    }
