package org.mili.evolution;

import java.util.List;

/**
 * @author Michael Lieshoff, 01.04.16
 */
public interface SelectionMethod {

    List<Chromosom> select(Configuration configuration, List<Chromosom> chromosoms);

}
