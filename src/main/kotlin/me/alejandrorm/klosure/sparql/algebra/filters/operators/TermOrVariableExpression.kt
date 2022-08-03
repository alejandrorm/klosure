package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

class TermOrVariableExpression(val termOrVariable: TermOrVariable) : Expression {
    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        TODO("Not yet implemented")
    }
}