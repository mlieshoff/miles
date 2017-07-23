package org.mili.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Michael Lieshoff, 30.03.16
 */
public class Population {

    private List<Chromosom> chromosoms = new ArrayList<>();

    private final Chromosom sampleChromosom;

    private final int populationSize;

    private final Configuration configuration;

    public Population(Chromosom sampleChromosom, int populationSize, Configuration configuration) {
        this.sampleChromosom = sampleChromosom;
        this.populationSize = populationSize;
        this.configuration = configuration;
    }

    public void populate() {
        chromosoms.clear();
        for (int i = 0; i < populationSize; i ++) {
            Chromosom chromosom = new Chromosom(configuration);
            List<Gene> genes = sampleChromosom.getGenes();
            Collections.shuffle(genes);
            chromosom.setGenes(genes);
            chromosoms.add(chromosom);
        }
    }

    public Chromosom getFittest() {
        Map<Integer, Integer> counts = new TreeMap<>();
        double d = 0.0;
        double delta = configuration.getFitnessDelta();
        Chromosom fittest = null;
        for (Chromosom chromosom : chromosoms) {
            double fitness = chromosom.getFitness();
            if (fitness > d) {
                d = fitness;
            }
            if (fitness >= delta) {
                fittest = chromosom;
            }

            int norm = Integer.valueOf(String.valueOf(d).replace("0.", "").substring(0, 6));
            Integer count = counts.get(norm);
            if (count == null) {
                count = 0;
            }
            counts.put(norm, ++ count);
        }
        /*
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            System.out.printf("%4d %4d\n", entry.getKey(), entry.getValue());
        }
        */
        System.out.println();
        return fittest;
    }

    public List<Chromosom> getTopFittest(double delta) {
        List<Chromosom> list = new ArrayList<>();
        Collections.sort(list, new Comparator<Chromosom>() {
            @Override
            public int compare(Chromosom o1, Chromosom o2) {
                return Double.compare(o2.getFitness(), o1.getFitness());
            }
        });
        for (Chromosom chromosom : chromosoms) {
            if (chromosom.getFitness() >= delta) {
                list.add(chromosom);
            }
        }
        return list;
    }

    public int size() {
        return chromosoms.size();
    }

    public void evolve() {
        List<Chromosom> sibblings = cross();
        List<Chromosom> mutations = mutate();
        Set<Chromosom> unfittest = killTheUnfittest(sibblings.size());
        chromosoms.addAll(sibblings);
    }

    private List<Chromosom> mutate() {
        return configuration.getMutationMethod().mutate(
                configuration,
                chromosoms
        );
    }

    private List<Chromosom> cross() {
        return configuration.getCrossingMethod().cross(
                configuration,
                configuration.getSelectionMethod().select(configuration, chromosoms)
        );
    }

    private Set<Chromosom> killTheUnfittest(int size) {
        Set<Chromosom> set = new HashSet<>();
        List<Chromosom> list = new ArrayList<>(chromosoms);
        Collections.sort(list, new Comparator<Chromosom>() {
            @Override
            public int compare(Chromosom o1, Chromosom o2) {
                return Double.compare(Math.abs(o1.getFitness()), Math.abs(o2.getFitness()));
            }
        });
        for (int i = 0; i < size; i ++) {
            Chromosom chromosom = list.get(i);
            set.add(chromosom);
            chromosoms.remove(chromosom);
        }
        return set;
    }

}
