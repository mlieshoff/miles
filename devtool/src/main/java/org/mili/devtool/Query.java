package org.mili.devtool;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Michael Lieshoff, 28.07.17
 */
public class Query {

    private final Set<String> tokens = new HashSet<>();

    public Query(String s) {
        if (StringUtils.isNotBlank(s)) {
            s = s.trim();
            if (StringUtils.isNotBlank(s)) {
                String[] array = s.split(",");
                for (String token : array) {
                    tokens.add(token.trim());
                }
            }
        }
    }

    public boolean match(Entry entry) {
        return true;
    }

}
