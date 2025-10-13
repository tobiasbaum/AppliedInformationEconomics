package de.set.aie.base;

/**
 * Ein syntaktischer Ausdruck, um zusammengesetzte Zufallsvariablen einfach hinschreiben zu können
 * (im Idealfall mit Operator-Overloading in Kotlin).
 */
public interface SyntacticExpression {

    public default SyntacticExpression minus(SyntacticExpression v1) {
        return new SyntacticExpression() {
            @Override
            public RandomVariable instantiate(Model.Instance inst) {
                return this.instantiate(inst).minus(v1.instantiate(inst));
            }
        };
    }

    public default SyntacticExpression conditionFor(SyntacticExpression v1) {
        return new SyntacticExpression() {
            @Override
            public RandomVariable instantiate(Model.Instance inst) {
                return Distributions.conditional(this.instantiate(inst), v1.instantiate(inst));
            }
        };
    }

    public abstract RandomVariable instantiate(Model.Instance inst);

}
