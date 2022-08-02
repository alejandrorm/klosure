package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class OrExpression(val expression1: Expression, val expression2: Expression) : Expression {
    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        val v1 = getEffectiveBooleanValue(expression1.eval(solution, graph))
        val v2 = getEffectiveBooleanValue(expression2.eval(solution, graph))

        if (v1 == null && v2 == false) return null
        if (v2 == null && v1 == false) return null
        if (v1 == null && v2 == true) return DataTypes.TRUE
        if (v2 == null && v1 == true) return DataTypes.TRUE
        if (v1 == null && v2 == null) return null
        return if (v1 == true || v2 == true) DataTypes.TRUE else DataTypes.FALSE
    }
}
