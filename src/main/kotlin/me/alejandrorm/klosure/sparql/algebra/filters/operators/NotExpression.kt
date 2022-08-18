package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.CompositeExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class NotExpression(val expression: Expression) : CompositeExpression(listOf(expression)) {
    override fun toString(): String {
        return "!($expression)"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        return when (getEffectiveBooleanValue(expression.eval(solution,activeGraph,graphs))) {
            true -> DataTypes.FALSE
            false -> DataTypes.TRUE
            null -> null
        }
    }

    override fun evalGroup(
        solution: SolutionMapping,
        group: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): NodeId? {
        return when (getEffectiveBooleanValue(expression.evalGroup(solution, group,activeGraph,graphs))) {
            true -> DataTypes.FALSE
            false -> DataTypes.TRUE
            null -> null
        }
    }
}
