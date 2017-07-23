package org.mili.evolution;

/**
 * @author Michael Lieshoff, 01.04.16
 */
public class Configuration {

    private final SelectionMethod selectionMethod;

    private final MutationMethod mutationMethod;

    private final CrossingMethod crossingMethod;

    private final FitnessFunction fitnessFunction;

    private final double fitnessDelta;

    public Configuration(FitnessFunction fitnessFunction, double fitnessDelta, SelectionMethod selectionMethod,
            CrossingMethod crossingMethod, MutationMethod mutationMethod) {
        this.fitnessFunction = fitnessFunction;
        this.fitnessDelta = fitnessDelta;
        this.selectionMethod = selectionMethod;
        this.crossingMethod = crossingMethod;
        this.mutationMethod = mutationMethod;
    }

    public FitnessFunction getFitnessFunction() {
        return fitnessFunction;
    }

    public double getFitnessDelta() {
        return fitnessDelta;
    }

    public CrossingMethod getCrossingMethod() {
        return crossingMethod;
    }

    public SelectionMethod getSelectionMethod() {
        return selectionMethod;
    }

    public MutationMethod getMutationMethod() {
        return mutationMethod;
    }

}
