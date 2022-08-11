package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class NotExpression(val expression: Expression) : Expression {
    override fun toString(): String {
        return "!($expression)"
    }
    override fun eval(solution: SolutionMapping): NodeId? {
        return when (getEffectiveBooleanValue(expression.eval(solution))) {
            true -> DataTypes.FALSE
            false ->DataTypes.TRUE
            null -> null
        }
    }
}
