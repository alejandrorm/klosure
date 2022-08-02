package me.alejandrorm.klosure.sparql.algebra.filters.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class UnsupportedBuiltinExpression(val name: String): Expression {
    override fun eval(solution: SolutionMapping, graph: Graph): NodeId? {
        throw UnsupportedOperationException("Unsupported builtin expression '$name'")
    }
}