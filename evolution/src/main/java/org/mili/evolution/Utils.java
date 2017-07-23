package org.mili.evolution;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Michael Lieshoff, 30.03.16
 */
public class Utils {

    public static int number(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
