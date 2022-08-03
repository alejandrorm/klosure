package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class FilterOperator(val expression: Expression) : AlgebraOperator {
    override fun toString(): String {
        return "Filter($expression)"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.filter {
            getEffectiveBooleanValue(expression.eval(it, graph)) == true
        }
    }
}