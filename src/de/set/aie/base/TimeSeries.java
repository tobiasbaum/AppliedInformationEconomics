package de.set.aie.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

/**
 * Eine Zeitreihe mit einer Zufallsvariable je diskretem Zeitpunkt (z.B. je Jahr).
 * Diese Klasse gehört zur Modellebene.
 * Eine Zeitreihe ist konzeptionell vergleichbar mit einem Vektor von Zufallsvariablen.
 */
public abstract class TimeSeries {

    /**
     * Summiert die Werte der Zeitreihe für den übergebenen Zeitraum auf.
     *
     * @param fromTime Beginn des Zeitraums (inkl.)
     * @param toTime   Ende des Zeitraums (inkl.)
     */
    public final Function<Model.Instance, RandomVariable> collapse(int fromTime, int toTime) {
        assert fromTime <= toTime;
        return (Model.Instance inst) -> {
            RandomVariable sum = getFor(fromTime).apply(inst);
            for (int i = fromTime + 1; i <= toTime; i++) {
                sum = sum.plus(getFor(i).apply(inst));
            }
            return sum;
        };
    }

    /**
     * Komponentenweise Summe aus zwei Zeitreihen.
     */
    public final TimeSeries plus(TimeSeries other) {
        return new CombinedTimeSeries(this, other, RandomVariable::plus);
    }

    /**
     * Komponentenweise Differenz aus zwei Zeitreihen.
     */
    public final TimeSeries minus(TimeSeries other) {
        return new CombinedTimeSeries(this, other, RandomVariable::minus);
    }

    /**
     * Komponentenweises Maximum aus zwei Zeitreihen.
     */
    public final TimeSeries max(TimeSeries other) {
        return new CombinedTimeSeries(this, other, RandomVariable::max);
    }

    /**
     * Quotient aus einer Zeitreihe und einer Zufallsvariable.
     */
    public TimeSeries div(String factor) {
        return new TransformedTimeSeriesVar(this, factor, RandomVariable::div);
    }

    /**
     * Produkt aus einer Zeitreihe und einer Zufallsvariable.
     */
    public TimeSeries times(String factor) {
        return new TransformedTimeSeriesVar(this, factor, RandomVariable::times);
    }

    /**
     * Produkt aus einer Zeitreihe und einem Skalar.
     */
    public TimeSeries times(double factor) {
        return new TransformedTimeSeriesConst(
                this, Distributions.fixed(factor, Unit.scalar()), RandomVariable::times);
    }

    /**
     * Bestimmt die "Ableitung" der Zeitreihe (Differenz zwischen aktuellem Zeitpunkt und vorherigem Zeitpunkt).
     */
    public TimeSeries differential() {
        return new DifferentialTimeSeries(this);
    }

    /**
     * Liefert eine Zeitreihe, die ab dem übergebenen Zeitpunkt mit der übergebenen Rate schrumpft.
     */
    public TimeSeries shrinkAfter(String yearsUntil, String shrinkRate) {
        return new ShrinkAfterTimeSeries(this, yearsUntil, shrinkRate);
    }

    /**
     * Liefert den Wert für den übergebenen Zeitpunkt (z.B. Jahr).
     */
    public abstract Function<Model.Instance, RandomVariable> getFor(int time);

    /**
     * Gibt die übergebenen Zeitreihen als CSV aus (jeweils den Mittelwert).
     */
    public static void printTable(File target, Model m, int fromTime, int toTime, Map<String, TimeSeries> toPrint)
        throws IOException {

        try (FileWriter fw = new FileWriter(target)) {
            // Header ausgeben
            fw.write("time");
            for (String s : toPrint.keySet()) {
                fw.write(";" + s);
            }
            fw.write("\n");
            // Zeilen ausgeben
            Model.Instance instance = m.instantiate();
            for (int i = fromTime; i <= toTime; i++) {
                fw.write(Integer.toString(i));
                for (TimeSeries t : toPrint.values()) {
                    fw.write(';');
                    Mean mean = t.getFor(i).apply(instance).sample(42, 1000).mean();
                    fw.write(Double.toString(mean.get()).replace('.', ','));
                }
                fw.write("\n");
            }
        }
    }

}
