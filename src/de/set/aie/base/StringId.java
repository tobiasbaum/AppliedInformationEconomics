package de.set.aie.base;

/**
 * Variablenbezeichnung auf Basis eines Strings.
 */
public class StringId implements VarId {
    private String id;

    public StringId(String id) {
        this.id = id.intern();
    }

    public String toString() {
        return id;
    }

    public int hashCode() {
        return id.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof StringId)) {
            return false;
        }
        return this.id.equals(((StringId) obj).id);
    }
}
