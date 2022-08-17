package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.BlankId
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.CompositeExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class IsBlank(val expression: Expression) : CompositeExpression(listOf(expression)) {
    override fun eval(solution: SolutionMapping): NodeId? {
        val v1 = expression.eval(solution) ?: return null
        if (v1 is BlankId) return DataTypes.TRUE
        return DataTypes.FALSE
    }
}
