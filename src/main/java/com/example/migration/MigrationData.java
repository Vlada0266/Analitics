package com.example.migration;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MigrationData {
    private final IntegerProperty year;
    private final DoubleProperty immigrants;
    private final DoubleProperty emigrants;

    public MigrationData(int year, double immigrants, double emigrants) {
        this.year       = new SimpleIntegerProperty(year);
        this.immigrants = new SimpleDoubleProperty(immigrants);
        this.emigrants  = new SimpleDoubleProperty(emigrants);
    }

    // Year
    public int getYear() {
        return year.get();
    }
    public IntegerProperty yearProperty() {
        return year;
    }

    // Immigrants
    public double getImmigrants() {
        return immigrants.get();
    }
    public DoubleProperty immigrantsProperty() {
        return immigrants;
    }

    // Emigrants
    public double getEmigrants() {
        return emigrants.get();
    }
    public DoubleProperty emigrantsProperty() {
        return emigrants;
    }
}
