package org.mili.devtool;

import java.util.List;

/**
 * @author Michael Lieshoff, 28.07.17
 */
public interface Connector {

    List<Entry> search(String query);

}
