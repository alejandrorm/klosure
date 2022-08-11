package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping

class SolutionModifier(val limit: Limit?) : AlgebraOperator {
    override fun toString(): String {
        return "SolutionModifier(limit=$limit)"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return limit?.eval(solutions, graph)?:solutions
    }

    override fun hasFilter(): Boolean {
        return false
    }

}