package me.alejandrorm.klosure.sparql.algebra.aggregates

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class Min(val expression: Expression) : AggregateExpression {
    override fun toString(): String {
        return "MIN($expression)"
    }

    override fun eval(solution: SolutionMapping): NodeId {
        throw IllegalStateException("MIN should not be evaluated")
    }

    override fun evalGroup(solution: SolutionMapping, group: Sequence<SolutionMapping>): NodeId? {
        var min: NodeId? = null
        group.forEach { solution ->
            val value = expression.eval(solution) ?: return null
            if (min == null) {
                min = value
            } else {
                if (value < min!!) {
                    min = value
                }
            }
        }
        return min
    }
}
