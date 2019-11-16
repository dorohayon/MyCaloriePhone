package com.example.akiscaloriephone.Database;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class SportEntry implements Serializable {
    private String name;
    LinkedHashMap<String,Double> levelAndMET;

    public SportEntry(String name, LinkedHashMap<String,Double> levelAndMET) {
        this.name = name;
        this.levelAndMET = levelAndMET;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<String,Double> getLevelAndMET() {
        return levelAndMET;
    }

    public void setLevelAndMET(LinkedHashMap<String,Double> levelAndMET) {
        this.levelAndMET = levelAndMET;
    }
}




