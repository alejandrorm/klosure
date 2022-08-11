package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class NegativeExpression(val expression: Expression) : Expression {
    override fun toString(): String {
        return "!($expression)"
    }

    override fun eval(solution: SolutionMapping): NodeId? {
        TODO("Not yet implemented")
    }
}