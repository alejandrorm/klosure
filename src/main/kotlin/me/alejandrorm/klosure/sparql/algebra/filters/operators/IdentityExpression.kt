package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.CompositeExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class IdentityExpression(val e: Expression) : CompositeExpression(listOf(e)) {
    override fun toString(): String {
        return e.toString()
    }

    override fun eval(solution: SolutionMapping): NodeId? {
        return e.eval(solution)
    }
}
