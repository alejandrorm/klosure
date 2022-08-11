package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping

class Limit(val limit: Int, val offset: Int) : AlgebraOperator {
    override fun toString(): String {
        return "Limit(limit=$limit,offset=$offset)"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.drop(offset).take(limit)
    }

    override fun hasFilter(): Boolean {
        return false
    }
}