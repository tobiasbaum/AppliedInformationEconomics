/**
 * Copyright 2021-2022 SET GmbH, Tobias Baum.
 *
 * This file is part of AppliedInformationEconomics.
 *
 * AppliedInformationEconomics is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AppliedInformationEconomics is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package de.set.aie.models;

import java.util.concurrent.ExecutionException;

import de.set.aie.base.Distributions;
import de.set.aie.base.Model;
import de.set.aie.base.Model.Instance;
import de.set.aie.base.Quantity;
import de.set.aie.base.RandomSource;
import de.set.aie.base.RandomVariable;
import de.set.aie.base.SimulationRun;
import de.set.aie.base.Unit;

public class TestModel {

    private static class EqRv extends RandomVariable {
        @Override
        public Quantity observe(final RandomSource r, final SimulationRun run) {
            return Quantity.of(r.nextDouble(), Unit.scalar());
        }

        @Override
        public Unit getUnit() {
            return Unit.scalar();
        }

        @Override
        public String getType() {
            return "D";
        }

    }

    public static void main(final String[] args) throws InterruptedException, ExecutionException {
        final Unit EUR = Unit.of("EUR");
        final Model m = new Model();
        m.addRaw("p", new EqRv());
        m.addRaw("play", (final Instance i) -> Distributions.conditional(i.get("p"),
                Distributions.fixed(Quantity.of(12, EUR)),
                Distributions.fixed(Quantity.of(-12, EUR))));
//        m.add("v1", Distributions.normal(1, 21, Unit.of("EUR")));
//        m.add("v2", Distributions.normal(2, 22, Unit.of("EUR")));
//        m.add("v1Excess", (final Instance i) -> i.get("v1").minus(i.get("v2")));
        m.addRaw("notPlay", Distributions.fixed(Quantity.of(0, "EUR")));

        m.analyze(1234, "play", "notPlay");
    }

}
