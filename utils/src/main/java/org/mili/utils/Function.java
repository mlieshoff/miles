package org.mili.utils;

/**
 * @author Michael Lieshoff
 */
public interface Function<I, O> {

    O execute(I in);

}
