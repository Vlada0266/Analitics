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

    public List<PopulationData> getMovingAverageForecast(List<PopulationData> data, int windowSize, int forecastYears) {
        return populationService.calculateMovingAverageForecast(data, windowSize, forecastYears);
    }

    public PopulationService.GrowthStats calculateGrowthStats(List<PopulationData> data) {
        return populationService.calculateGrowthStats(data);
    }
}