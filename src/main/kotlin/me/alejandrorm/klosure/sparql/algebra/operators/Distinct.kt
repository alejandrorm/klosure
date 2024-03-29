package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

class Distinct : AlgebraOperator {
    override fun toString(): String {
        return "DISTINCT"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping> {
        return solutions.distinct()
    }

    override fun hasFilter(): Boolean = false
}
