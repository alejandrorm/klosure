package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

class Identity : AlgebraOperator {
    override fun toString(): String =
        "Identity()"

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): Sequence<SolutionMapping> {
        return solutions
    }

    override fun hasFilter(): Boolean = false
}
