package de.set.aie.base;

import java.util.function.Function;

/**
 * Ein syntaktischer Ausdruck, um zusammengesetzte Zufallsvariablen einfach hinschreiben zu können
 * (im Idealfall mit Operator-Overloading in Kotlin).
 */
public interface SyntacticExpression extends Function<Model.Instance, RandomVariable> {

    public default SyntacticExpression minus(SyntacticExpression v1) {
        return new SyntacticExpression() {
            @Override
            public RandomVariable apply(Model.Instance inst) {
                return SyntacticExpression.this.apply(inst).minus(v1.apply(inst));
            }
        };
    }

    public default SyntacticExpression conditionFor(SyntacticExpression v1) {
        return new SyntacticExpression() {
            @Override
            public RandomVariable apply(Model.Instance inst) {
                return Distributions.conditional(
                        SyntacticExpression.this.apply(inst), v1.apply(inst));
            }
        };
    }

}
