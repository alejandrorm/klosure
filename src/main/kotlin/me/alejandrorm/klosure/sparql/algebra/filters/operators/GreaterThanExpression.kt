package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.CompositeExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class GreaterThanExpression(val left: Expression, val right: Expression) : CompositeExpression(listOf(left, right)) {
    override fun toString(): String {
        return "($left > $right)"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        TODO("Not yet implemented")
    }
}
