package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class BasicGraphPattern(val patterns: List<TriplePattern>) : TriplePattern, AlgebraOperator {
    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
        return patterns.fold(sequenceOf(solution)) { acc, pattern ->
            pattern.eval(acc, graph)
        }
    }

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return patterns.fold(solutions) { acc, pattern ->
            pattern.eval(acc, graph)
        }
    }

    override fun getVariables(): Set<Variable> {
        return patterns.flatMap { it.getVariables() }.toSet()
    }
}
