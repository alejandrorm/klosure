package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.*
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class IsBlank(val expression: Expression) : Expression {
    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val v1 = expression.eval(solution, graph) ?: return null
        if (v1 is BlankId) return DataTypes.TRUE
        return DataTypes.FALSE
    }
}
