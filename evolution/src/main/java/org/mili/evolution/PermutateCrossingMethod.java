package org.mili.evolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Michael Lieshoff, 01.04.16
 */
public class PermutateCrossingMethod implements CrossingMethod {

    @Override
    public List<Chromosom> cross(Configuration configuration, List<Chromosom> chromosoms) {
        List<Chromosom> crossings = new ArrayList<>();
        for (int i = 0, n = chromosoms.size(); i < n - 1; i = i + 2) {
            Chromosom father = chromosoms.get(i);
            List<Gene> sibblingGenes = new ArrayList<>(father.getGenes());
            int start = Utils.number(0, sibblingGenes.size());
            int end = Utils.number(start, start + (sibblingGenes.size() - start));
            List<Gene> permuated = sibblingGenes.subList(start, end);
            Collections.shuffle(permuated);
            int index = 0;
            for (int ii = start; ii < end; ii ++) {
                sibblingGenes.set(ii, permuated.get(index));
                index ++;
            }
            Chromosom sibbling = new Chromosom(configuration);
            sibbling.setGenes(sibblingGenes);
            crossings.add(sibbling);
        }
        return crossings;
    }

}
