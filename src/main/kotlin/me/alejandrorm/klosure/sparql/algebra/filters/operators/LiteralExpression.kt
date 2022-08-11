package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class LiteralExpression(val id: NodeId): Expression {
    override fun eval(solution: SolutionMapping): NodeId? {
        return id
    }

    override fun toString(): String {
        return id.toString()
    }
}