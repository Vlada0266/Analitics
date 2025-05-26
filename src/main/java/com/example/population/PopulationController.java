package com.example.population;

import java.io.File;
import java.util.List;

public class PopulationController {
    private final PopulationService populationService;

    public PopulationController() {
        this.populationService = new PopulationService();
    }

    public List<PopulationData> loadPopulationData(File file) {
        return populationService.loadDataFromCSV(file);
    }

    public List<Double> forecast(List<PopulationData> data, int windowSize, int years) {
        return populationService.calculateForecast(data, windowSize, years);
    }
}
