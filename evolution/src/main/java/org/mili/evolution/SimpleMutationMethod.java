package org.mili.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Michael Lieshoff, 01.04.16
 */
public class SimpleMutationMethod implements MutationMethod {

    @Override
    public List<Chromosom> mutate(Configuration configuration, List<Chromosom> chromosoms) {
        List<Chromosom> mutationPool = new ArrayList<>(chromosoms);
        Collections.sort(mutationPool, new Comparator<Chromosom>() {
            @Override
            public int compare(Chromosom o1, Chromosom o2) {
                return Double.compare(o2.getFitness(), o1.getFitness());
            }
        });
        List<Chromosom> mutations = new ArrayList<>();
        int numberOfMutations = Utils.number(1, chromosoms.size() / 4);
        int count = 0;
        for (int i = numberOfMutations, n = chromosoms.size(); i < n; i ++) {
            Chromosom chromosom = chromosoms.get(i);
            count ++;
            List<Gene> genes = chromosom.getGenes();
            int index1 = Utils.number(0, genes.size()-1);
            int index2 = Utils.number(0, genes.size()-1);
            Gene temp = genes.get(index2);
            genes.set(index2, genes.get(index1));
            genes.set(index1, temp);
            chromosom.setGenes(genes);
            mutations.add(chromosom);
            if (count >= numberOfMutations) {
                break;
            }
        }
        return mutations;
    }
}
