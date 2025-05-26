package com.example.population;

public class PopulationData {
    private int year;
    private double population;

    public PopulationData(int year, double population) {
        this.year = year;
        this.population = population;
    }

    public int getYear() {
        return year;
    }

    public double getPopulation() {
        return population;
    }
}
