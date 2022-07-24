package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

interface TriplePattern {
    // TODO should this be Iterable or Sequence?
    fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping>

    fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping>

    fun getVariables(): Set<Variable>
}
