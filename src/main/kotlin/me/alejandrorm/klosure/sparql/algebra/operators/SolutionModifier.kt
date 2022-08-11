package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

class SolutionModifier(val limit: Limit?) : AlgebraOperator {
    override fun toString(): String {
        return "SolutionModifier(limit=$limit)"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping> {
        return limit?.eval(solutions, activeGraph, graphs)?:solutions
    }

    override fun hasFilter(): Boolean {
        return false
    }

}