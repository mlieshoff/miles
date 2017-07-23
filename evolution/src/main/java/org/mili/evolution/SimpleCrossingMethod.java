package org.mili.evolution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff, 01.04.16
 */
public class SimpleCrossingMethod implements CrossingMethod {

    @Override
    public List<Chromosom> cross(Configuration configuration, List<Chromosom> chromosoms) {
        List<Chromosom> crossings = new ArrayList<>();
        for (int i = 0, n = chromosoms.size(); i < n - 1; i = i + 2) {
            Chromosom father = chromosoms.get(i);
            Chromosom mother = chromosoms.get(i + 1);
            List<Gene> fatherGenes = father.getGenes();
            List<Gene> sibblingGenes = mother.getGenes();
            int start = Utils.number(0, fatherGenes.size());
            int end = Utils.number(start, start + (fatherGenes.size() - start));
            for (int ii = start; ii < end; ii ++) {
                sibblingGenes.set(ii, fatherGenes.get(ii));
            }
            Chromosom sibbling = new Chromosom(configuration);
            sibbling.setGenes(sibblingGenes);
        }
        return crossings;
    }

}
