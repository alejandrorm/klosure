package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.FalseLiteral
import me.alejandrorm.klosure.model.Node
import me.alejandrorm.klosure.model.TrueLiteral
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class NotExpression(val expression: Expression) : Expression {
    override fun eval(solution: SolutionMapping): Node? {
        return when (getEffectiveBooleanValue(expression.eval(solution))) {
            null -> null
            TrueLiteral -> FalseLiteral
            FalseLiteral -> TrueLiteral
        }
    }
}
