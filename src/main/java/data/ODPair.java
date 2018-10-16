package data;

import java.util.Objects;

public class ODPair {
    public long origin;
    public long destination;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ODPair odPair = (ODPair) o;
        return origin == odPair.origin &&
                destination == odPair.destination;
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination);
    }

    public ODPair(long origin, long destination) {
        this.origin = origin;
        this.destination = destination;
    }
}
