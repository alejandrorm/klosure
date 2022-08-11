package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

interface AlgebraOperator {
    fun eval(solutions: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping>

    fun hasFilter(): Boolean
}
