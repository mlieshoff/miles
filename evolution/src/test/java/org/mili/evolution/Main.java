package org.mili.evolution;

import java.util.Comparator;
import java.util.List;

/**
 * @author Michael Lieshoff, 29.03.16
 */
public class Main {

    private static final int N = 10;
    private static final int M = 100000;

    static class Fitness implements FitnessFunction {
        @Override
        public double evaluate(Chromosom chromosom) {
            double fitness = 0.0;
            List<Gene> genes = chromosom.getGenes();
            for (int i = 0, n = genes.size(); i < n; i = i + 2) {
                Group group1 = (Group) genes.get(i).getObject();
                Group group2 = (Group) genes.get(i + 1).getObject();
                if (group1.getRating() >= group2.getRating()) {
                    fitness += group2.getRating() / (double) group1.getRating();
                } else {
                    fitness += group1.getRating() / (double) group2.getRating();
                }
            }
            return fitness / (genes.size() / 2);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Configuration configuration = new Configuration(
                new Fitness(),
                0.91,
                new SimpleSelectionMethod(),
                new PermutateCrossingMethod(),
                new SimpleMutationMethod()
        );

        Chromosom sampleChromosom = new Chromosom(configuration);
        for (int i = 0; i < N; i++) {
            Group group = new Group(String.valueOf(i), Utils.number(1000, 3000));
            sampleChromosom.addGene(new Gene(group));
        }

        sampleChromosom.sort(new Comparator<Gene>() {
            @Override
            public int compare(Gene o1, Gene o2) {
                Group group1 = (Group) o1.getObject();
                Group group2 = (Group) o2.getObject();
                return Integer.compare(group2.getRating(), group1.getRating());
            }
        });

        for (int rounds = 0; rounds < 5; rounds ++) {
            Genotype genotype = new Genotype(sampleChromosom, M, configuration);
            genotype.evolve();
            List<Chromosom> top = genotype.getTop(configuration.getFitnessDelta());
            for (int i = 0, n = top.size(); i < n; i++) {
                Chromosom chromosom = top.get(i);
                System.out.println("*    " + chromosom.getFitness());
                List<Gene> genes = chromosom.getGenes();
                for (int ii = 0, nn = genes.size(); ii < nn; ii = ii + 2) {
                    Gene gene1 = genes.get(ii);
                    Gene gene2 = genes.get(ii + 1);
                    Group group1 = (Group) gene1.getObject();
                    Group group2 = (Group) gene2.getObject();
                    System.out.printf("%s (%s) VS. %s (%s)\n", group1.getRating(), group1.getName(), group2.getRating(), group2.getName());
                }
                break;
            }
        }
    }

}
