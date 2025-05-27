package com.example.migration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MigrationService {

    public List<MigrationData> loadDataFromCSV(File file) {
        List<MigrationData> dataList = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                String[] parts = line.split("[,;\\t]");
                if (parts.length >= 2) {
                    int year = Integer.parseInt(parts[0].trim());
                    double migrationValue = Double.parseDouble(parts[1].trim());
                    dataList.add(new MigrationData(year, migrationValue));
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
