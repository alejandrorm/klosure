package me.alejandrorm.klosure.sparql.algebra.aggregates

import me.alejandrorm.klosure.sparql.algebra.filters.Expression

interface AggregateExpression: Expression {
    override fun isAggregate(): Boolean = true
}