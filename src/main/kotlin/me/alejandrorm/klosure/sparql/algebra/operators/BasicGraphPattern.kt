package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class BasicGraphPattern(val patterns: List<TriplePattern>) : TriplePattern, GraphPattern {
    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        return patterns.fold(listOf(solution)) { acc, pattern ->
            acc.flatMap { pattern.eval(it, graph) }
        }
    }

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> {
        return patterns.flatMap { it.getVariables() }.toSet()
    }
}
