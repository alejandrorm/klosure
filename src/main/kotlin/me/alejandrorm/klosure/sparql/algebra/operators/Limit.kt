package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.GroupedSolutionMapping
import me.alejandrorm.klosure.sparql.SolutionMapping

class Limit(val limit: Int, val offset: Int)  {
    override fun toString(): String {
        return "Limit(limit=$limit,offset=$offset)"
    }

    fun eval(solutions: Sequence<GroupedSolutionMapping>): Sequence<GroupedSolutionMapping> {
        return solutions.drop(offset).take(limit)
    }
}