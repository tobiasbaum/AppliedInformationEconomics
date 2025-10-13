package de.set.aie.base;

/**
 * Variablenart: Zur Information, um in der Auflistung besser erkennen zu können, um was für eine
 * Variable es sich handelt.
 */
public enum VarKind {
    /**
     * "Distribution": Ein Wert direkt aus einer (definierten, nicht-festen) Zufallsverteilung.
     */
    D,
    /**
     * "Combined": Ein Wert, der auf anderen Zufallsvariablen aufbaut.
     */
    C,
    /**
     * "Fixed": Ein fester Wert.
     */
    F,
    /**
     * "Uncertain": Ein Wert aus einer unbekannten Verteilung.
     */
    U
}
