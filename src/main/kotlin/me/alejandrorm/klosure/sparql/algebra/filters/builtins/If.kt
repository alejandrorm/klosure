package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.aggregates.CompositeExpression
import me.alejandrorm.klosure.sparql.algebra.filters.Expression
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class If(val e1: Expression, val e2: Expression, val e3: Expression) : CompositeExpression(listOf(e1, e2, e3)) {
    override fun toString(): String {
        return "if($e1, $e2, $e3)"
    }

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        val v = getEffectiveBooleanValue(e1.eval(solution,activeGraph,graphs)) ?: return null

        return if (v) e2.eval(solution,activeGraph,graphs) else e3.eval(solution,activeGraph,graphs)
    }
}
