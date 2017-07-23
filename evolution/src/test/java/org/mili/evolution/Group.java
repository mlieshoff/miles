package org.mili.evolution;

/**
 * @author Michael Lieshoff, 30.03.16
 */
public class Group {

    private String name;
    private int rating;

    public Group(String name, int rating) {
        this.name = name;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", rating=" + rating +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;

        Group group = (Group) o;

        return !(name != null ? !name.equals(group.name) : group.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

}
