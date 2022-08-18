package me.alejandrorm.klosure.sparql.algebra.filters

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping

interface Expression {
    fun eval(solution: SolutionMapping, activeGraph: Graph, graphs: Graphs): NodeId?

    fun evalGroup(solution: SolutionMapping, group: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): NodeId? =
        eval(solution,activeGraph,graphs)

    fun isAggregate(): Boolean
}
