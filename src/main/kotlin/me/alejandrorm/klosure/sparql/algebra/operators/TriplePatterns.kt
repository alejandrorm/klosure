package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

object TriplePatterns {
    fun chain(
        patterns: List<TriplePattern>,
        currentSolutions: Iterable<SolutionMapping>,
        graph: Graph
    ): Iterable<SolutionMapping> {
        return if (patterns.isEmpty()) currentSolutions else {
            chain(patterns.drop(1), patterns[0].eval(currentSolutions, graph), graph)
        }
    }

    fun chain(
        patterns: List<TriplePattern>,
        graph: Graph
    ): Iterable<SolutionMapping> {
        val currentSolutions = listOf(SolutionMapping(getVariables(patterns), emptyMap()))
        return chain(patterns, currentSolutions, graph)
    }

    fun getVariables(patterns: List<TriplePattern>): Set<Variable> {
        // TODO cache
        return patterns.map { it.getVariables() }.reduce { s1, s2 -> s1 + s2 }
    }
}
