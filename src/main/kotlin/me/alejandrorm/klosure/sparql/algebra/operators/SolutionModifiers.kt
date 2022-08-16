package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.GroupedSolutionMapping
import me.alejandrorm.klosure.sparql.SolutionMapping

class SolutionModifiers(val groupBy: GroupBy?, val limit: Limit?) {
    override fun toString(): String {
        return "SolutionModifier(limit=$limit, groupBy=$groupBy)"
    }

    fun eval(
        solutions: Sequence<SolutionMapping>
    ): Sequence<GroupedSolutionMapping> {
        val s1 =
            groupBy?.eval(solutions) ?: solutions.map { GroupedSolutionMapping(it, emptySequence()) }
        return limit?.eval(s1) ?: s1
    }
}