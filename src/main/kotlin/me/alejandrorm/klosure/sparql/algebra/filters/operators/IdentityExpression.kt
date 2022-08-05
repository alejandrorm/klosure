package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class IdentityExpression(val e: Expression): Expression  {
    override fun toString(): String {
        return e.toString()
    }

    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        return e.eval(solution, graph)
    }
}