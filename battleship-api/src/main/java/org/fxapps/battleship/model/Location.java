package org.fxapps.battleship.model;

import java.util.HashSet;
import java.util.Set;

public class Location {

    private static Set<Location> cache = new HashSet<>();

    private int x;
    private int y;

    private Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private Location() {}

    public static Location initial() {
        return of(0, 0);
    }

    public static Location of(int x, int y) {
        return cache.stream()
                    .filter(l -> l.x == x && l.y == y)
                    .findFirst().orElseGet(() -> {
                        Location location = new Location(x, y);
                        cache.add(location);
                        return location;
                    });
    }

    public Location withX(int x) {
        return of(x, this.y);
    }

    public Location withY(int y) {
        return of(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Location [x=" + x + ", y=" + y + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Location other = (Location) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

}
