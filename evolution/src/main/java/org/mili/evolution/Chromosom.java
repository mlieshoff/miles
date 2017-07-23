package org.mili.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Michael Lieshoff, 30.03.16
 */
public class Chromosom {

    private Double fitness = null;

    private List<Gene> genes = new ArrayList<>();

    private Configuration configuration;

    public Chromosom(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<Gene> getGenes() {
        return new ArrayList<>(genes);
    }

    public void addGene(Gene gene) {
        genes.add(gene);
        fitness = null;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
        fitness = null;
    }

    public double getFitness() {
        if (fitness == null) {
            fitness = fitness();
        }
        return fitness;
    }

    private Double fitness() {
        return configuration.getFitnessFunction().evaluate(this);
    }

    @Override
    public String toString() {
        return "Chromosom{" +
                "fitness=" + getFitness() +
                ", genes=" + genes +
                '}';
    }

    public void sort(Comparator<Gene> comparator) {
        Collections.sort(genes, comparator);
    }

}
