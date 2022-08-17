package me.alejandrorm.klosure.sparql.algebra.aggregates

import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.IntegerValue
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class Count(val distinct: Boolean, val expression: Expression?) : AggregateExpression {
    override fun toString(): String {
        return "COUNT($expression)"
    }

    override fun eval(solution: SolutionMapping): NodeId {
        throw IllegalStateException("Count should not be evaluated")
    }

    override fun evalGroup(solution: SolutionMapping, group: Sequence<SolutionMapping>): NodeId {
        val c = expression?.let { group.map { expression.eval(it) }.count { v -> v != null } } ?: group.count()
        return LiteralId(c.toString(), IntegerValue(c.toBigInteger()))
    }
}
