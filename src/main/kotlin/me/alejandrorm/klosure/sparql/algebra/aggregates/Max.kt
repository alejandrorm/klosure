package me.alejandrorm.klosure.sparql.algebra.aggregates

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class Max(val expression: Expression) : AggregateExpression {
    override fun toString(): String {
        return "MAX($expression)"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId {
        throw IllegalStateException("MAX should not be evaluated")
    }

    override fun evalGroup(
        solution: SolutionMapping,
        group: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): NodeId? {
        var max: NodeId? = null
        group.forEach { solution ->
            val value = expression.eval(solution,activeGraph,graphs)
            if (max == null) {
                max = value
            } else if (value != null){
                if (value > max!!) {
                    max = value
                }
            }
        }
        return max
    }
}
