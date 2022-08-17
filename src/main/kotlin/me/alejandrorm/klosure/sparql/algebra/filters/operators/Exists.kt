package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.NonAggregateExpression
import me.alejandrorm.klosure.sparql.algebra.operators.AlgebraOperator

class Exists(val operator: AlgebraOperator): NonAggregateExpression {
    override fun eval(solution: SolutionMapping): NodeId? {
        TODO("Not yet implemented")
    }
}
