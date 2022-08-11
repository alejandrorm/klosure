package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

class Union(val operators: List<AlgebraOperator>) : AlgebraOperator {
    override fun toString(): String =
        "Union(${operators.toString()})"

    override fun eval(solutions: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping> {
        return operators.fold(emptySequence()) { acc, op -> acc + op.eval(solutions, activeGraph, graphs) }
    }

    override fun hasFilter(): Boolean = operators.any { it.hasFilter() }
}
