package org.mili.evolution;

/**
 * @author Michael Lieshoff, 30.03.16
 */
public class Gene {

    private Object object;

    public Gene(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "Gene{" +
                "object=" + object +
                '}';
    }

}
