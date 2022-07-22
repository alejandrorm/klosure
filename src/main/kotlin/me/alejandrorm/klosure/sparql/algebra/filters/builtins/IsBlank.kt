package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.BlankId
import me.alejandrorm.klosure.model.FalseLiteral
import me.alejandrorm.klosure.model.Node
import me.alejandrorm.klosure.model.TrueLiteral
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class IsBlank(val expression: Expression) : Expression {
    override fun eval(solution: SolutionMapping): Node? {
        val v1 = expression.eval(solution) ?: return null
        if (v1.id is BlankId) return TrueLiteral
        return FalseLiteral
    }
}
