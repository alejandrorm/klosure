package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.SolutionMapping

class Minus(val operator: AlgebraOperator) : AlgebraOperator {
    override fun toString(): String {
        return "Minus($operator)"
    }
    override fun eval(solutions: Sequence<SolutionMapping>, activeGraph: Graph, graphs: Graphs): Sequence<SolutionMapping> {
        return solutions.minus(operator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), activeGraph, graphs))
    }

    override fun hasFilter(): Boolean = operator.hasFilter()
}
