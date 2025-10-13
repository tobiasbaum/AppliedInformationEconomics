package de.set.aie.base;

/**
 * Eine ID für / ein Verweis auf eine Variable.
 * Muss equals, hashcode + toString implementieren, und zwar so, dass zwei gleiche IDs auch die gleiche
 * String-Darstellung haben.
 */
public interface VarId {

    public static VarId of(String id) {
        return new StringId(id);
    }

    /**
     * Erzeugt einen abgeleiteten Variablennamen, basierend auf dem aktuellen Namen und dem
     * übergebenen Suffix.
     */
    public default VarId subvar(String subId) {
        return of(this.toString() + "_" + subId);
    }

}
