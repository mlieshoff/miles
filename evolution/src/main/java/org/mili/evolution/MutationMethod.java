package org.mili.evolution;

import java.util.List;

/**
 * @author Michael Lieshoff, 01.04.16
 */
public interface MutationMethod {

    List<Chromosom> mutate(Configuration configuration, List<Chromosom> chromosoms);

}
