package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

interface TriplePattern {
    // TODO should this be Iterable or Sequence?
    fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping>

    fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping>

    fun getVariables(): Set<Variable>
}
