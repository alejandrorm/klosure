package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.aggregates.NonAggregateExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class VariableExpression(val variable: Variable): NonAggregateExpression {
    override fun eval(solution: SolutionMapping): NodeId? {
        return solution.boundVariables[variable]
    }

    override fun toString(): String {
        return variable.toString()
    }
}