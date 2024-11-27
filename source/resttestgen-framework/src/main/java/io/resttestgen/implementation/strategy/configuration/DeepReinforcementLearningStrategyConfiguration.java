package io.resttestgen.implementation.strategy.configuration;

import io.resttestgen.core.testing.StrategyConfiguration;

public class DeepReinforcementLearningStrategyConfiguration extends StrategyConfiguration {

    private long timeBudget = 3700;
    private String namedPipesPath = "/home/davide/Desktop/deeprest";
    private boolean disableDrl = false;
    private boolean intensification = true;
    private int intensificationProbability = 2; // 0-100
    private String fuzzer = "experience"; // "experience" or "nominal"

    public long getTimeBudget() {
        return timeBudget;
    }

    public void setTimeBudget(long timeBudget) {
        this.timeBudget = timeBudget;
    }

    public String getNamedPipesPath() {
        return namedPipesPath;
    }

    public void setNamedPipesPath(String namedPipesPath) {
        this.namedPipesPath = namedPipesPath;
    }

    public boolean isDisableDrl() {
        return disableDrl;
    }

    public void setDisableDrl(boolean disableDrl) {
        this.disableDrl = disableDrl;
    }

    public boolean isIntensification() {
        return intensification;
    }

    public void setIntensification(boolean intensification) {
        this.intensification = intensification;
    }

    public double getIntensificationProbability() {
        return intensificationProbability;
    }

    public void setIntensificationProbability(int intensificationProbability) {
        this.intensificationProbability = intensificationProbability;
    }

    public String getFuzzer() {
        return fuzzer;
    }

    public void setFuzzer(String fuzzer) {
        this.fuzzer = fuzzer;
    }
}
