package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.NonAggregateExpression

class LiteralExpression(val id: NodeId) : NonAggregateExpression {
    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId {
        return id
    }

    override fun toString(): String {
        return id.toString()
    }
}
