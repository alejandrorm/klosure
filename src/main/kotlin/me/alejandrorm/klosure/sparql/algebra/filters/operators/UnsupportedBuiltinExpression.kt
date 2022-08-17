package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.NonAggregateExpression

class UnsupportedBuiltinExpression(val name: String) : NonAggregateExpression {
    override fun eval(solution: SolutionMapping): NodeId? {
        throw UnsupportedOperationException("Unsupported builtin expression '$name'")
    }
}
