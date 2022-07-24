package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class SubjectTriplePattern : TriplePattern {
    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
        TODO("Not yet implemented")
    }

    override fun getVariables(): Set<Variable> {
        TODO("Not yet implemented")
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        TODO("Not yet implemented")
    }
}
