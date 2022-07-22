package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.FalseLiteral
import me.alejandrorm.klosure.model.Node
import me.alejandrorm.klosure.model.TrueLiteral
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class OrExpression(val expression1: Expression, val expression2: Expression) : Expression {
    override fun eval(solution: SolutionMapping): Node? {
        val v1 = getEffectiveBooleanValue(expression1.eval(solution))
        val v2 = getEffectiveBooleanValue(expression2.eval(solution))

        if (v1 == null && v2 == FalseLiteral) return null
        if (v2 == null && v1 == FalseLiteral) return null
        if (v1 == null && v2 == TrueLiteral) return TrueLiteral
        if (v2 == null && v1 == TrueLiteral) return TrueLiteral
        if (v1 == null && v2 == null) return null
        return if (v1 == TrueLiteral || v2 == TrueLiteral) TrueLiteral else FalseLiteral
    }
}
