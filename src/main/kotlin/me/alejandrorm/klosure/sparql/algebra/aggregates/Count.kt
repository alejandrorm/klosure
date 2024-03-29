package me.alejandrorm.klosure.sparql.algebra.aggregates

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.LiteralId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.IntegerValue
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class Count(val distinct: Boolean, val expression: Expression?) : AggregateExpression {
    override fun toString(): String {
        return "COUNT($expression)"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId {
        throw IllegalStateException("Count should not be evaluated")
    }

    override fun evalGroup(
        solution: SolutionMapping,
        group: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): NodeId {
        val c = if (distinct) {
            expression?.let { group.map { expression.eval(it, activeGraph, graphs) }.filterNotNull().toSet().size }
                ?: group.toSet().size
        } else {
            expression?.let { group.map { expression.eval(it, activeGraph, graphs) }.count { v -> v != null } }
                ?: group.count()
        }
        return LiteralId(c.toString(), IntegerValue(c.toBigInteger()))
    }
}
