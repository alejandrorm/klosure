package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class Bind(val expression: Expression, val variable: Variable) : AlgebraOperator {
    override fun toString(): String {
        return "BIND($expression AS $variable)"
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.map {
            val value = expression.eval(it, graph)
            if (value != null) {
                it.bind(variable, value)
            } else {
                it.addVariable(variable)
            }
        }
    }

    override fun hasFilter(): Boolean = false
}