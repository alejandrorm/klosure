package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.NonAggregateExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

class TermOrVariableExpression(val termOrVariable: TermOrVariable) : NonAggregateExpression {
    override fun eval(solution: SolutionMapping): NodeId? {
        val v = termOrVariable.resolve(solution)
        return if (v.isBound()) v.getTerm() else null
    }
}