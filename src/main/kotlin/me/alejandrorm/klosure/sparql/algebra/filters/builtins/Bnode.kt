package me.alejandrorm.klosure.sparql.algebra.filters.builtins

import me.alejandrorm.klosure.model.BlankId
import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.Expression

class Bnode(val expression: Expression?) : Expression {

    override fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId? {
        return if (expression != null) {
            val label = expression.eval(solution, activeGraph, graphs)

            label?.let { BlankId(label.toString()) }
        } else {
            activeGraph.generateAnonId()
        }
    }

    override fun evalGroup(
        solution: SolutionMapping,
        group: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): NodeId? {
        return if (expression != null) {
            val label = expression.evalGroup(solution, group, activeGraph, graphs)

            label?.let { BlankId(label.toString()) }
        } else {
            activeGraph.generateAnonId()
        }
    }

    override fun isAggregate(): Boolean {
        return expression?.isAggregate() ?: return false
    }
}
