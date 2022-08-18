package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class Filter(val expression: Expression) : AlgebraOperator {
    override fun toString(): String {
        return "Filter($expression)"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping> {
        return solutions.filter {
            val v = getEffectiveBooleanValue(expression.eval(it,activeGraph,graphs)) == true
            v
        }
    }

    override fun hasFilter(): Boolean = true
}
