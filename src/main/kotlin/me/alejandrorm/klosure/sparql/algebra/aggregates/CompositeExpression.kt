package me.alejandrorm.klosure.sparql.algebra.aggregates

import me.alejandrorm.klosure.sparql.algebra.filters.Expression

abstract class CompositeExpression(val expressions: List<Expression>) : Expression {
    override fun isAggregate(): Boolean {
        return expressions.any { it.isAggregate() }
    }
}
