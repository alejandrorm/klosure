package me.alejandrorm.klosure.sparql.algebra.aggregates

import me.alejandrorm.klosure.sparql.algebra.filters.Expression

interface NonAggregateExpression: Expression {
    override fun isAggregate(): Boolean = false
}