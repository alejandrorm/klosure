package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class CompiledSequencePath(val compiledPath: List<CompiledPath>) : CompiledPath {
    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        return compiledPath.fold(listOf(solution).asIterable()) {
                solutions, path ->
            path.eval(solutions, graph)
        }
    }

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> = compiledPath.fold(emptySet()) { v, p -> v + p.getVariables() }
}
