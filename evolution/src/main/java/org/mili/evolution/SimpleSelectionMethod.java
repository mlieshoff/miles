package org.mili.evolution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Lieshoff, 01.04.16
 */
public class SimpleSelectionMethod implements SelectionMethod {

    public List<Chromosom> select(Configuration configuration, List<Chromosom> chromosoms) {
        List<Chromosom> toSelect = new ArrayList<>(chromosoms);
        List<Chromosom> selections = new ArrayList<>();
        int upperSize = Math.round(chromosoms.size() / 2);
        int numberOfSelections = Utils.number(2, upperSize);
        for (int i = 0; i < numberOfSelections; i ++) {
            int index = Utils.number(0, toSelect.size() - 1);
            Chromosom selected = toSelect.get(index);
            selections.add(selected);
            toSelect.remove(selected);
        }
        return selections;
    }

}
