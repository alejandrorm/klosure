package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class LessOrEqualThanExpression(val left: Expression, val right: Expression) : Expression {
    override fun toString(): String {
        return "$left <= $right"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        TODO("Not yet implemented")
    }
}