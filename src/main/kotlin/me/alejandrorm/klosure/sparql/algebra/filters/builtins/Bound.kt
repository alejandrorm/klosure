package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.model.literals.DataTypes
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.aggregates.NonAggregateExpression

class Bound(val variable: Variable) : NonAggregateExpression {
    override fun eval(solution: SolutionMapping): NodeId {
        return if (solution.boundVariables.containsKey(variable)) DataTypes.TRUE else DataTypes.FALSE
    }
}
