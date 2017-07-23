package org.mili.evolution;

import java.util.List;

/**
 * @author Michael Lieshoff, 30.03.16
 */
public class Genotype {

    private Population population;

    private Chromosom best;

    private int steps;

    private Configuration configuration;

    public Genotype(Chromosom sampleChromosom, int populationSize, Configuration configuration) {
        this.configuration = configuration;
        population = new Population(sampleChromosom, populationSize, configuration);
        population.populate();
    }

    public void evolve() {
        steps = 0;
        while(notSolved()) {
            population.evolve();
            steps = steps + 1;
            if (population.size() <= 10) {
                break;
            }
        }
        System.out.println("steps: " + steps);
    }

    public Chromosom getBest() {
        return best;
    }

    private boolean notSolved() {
        best = population.getFittest();
        if (best != null) {
            System.out.println(best.getFitness());
        }
        return best == null;
    }

    public List<Chromosom> getTop(double delta) {
        return population.getTopFittest(delta);
    }

}
