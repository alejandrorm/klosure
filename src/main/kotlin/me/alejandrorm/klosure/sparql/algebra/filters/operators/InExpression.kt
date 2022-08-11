package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class InExpression(val e: Expression, val values: List<Expression>) : Expression {
    override fun toString(): String {
        return "($e IN ${values.joinToString(", ")})"
    }

    override fun eval(solution: SolutionMapping): NodeId? {
        TODO("Not yet implemented")
    }
}