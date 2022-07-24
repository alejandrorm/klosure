package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class CompiledSequencePath(val compiledPath: List<CompiledPath>) : CompiledPath {
    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
        return compiledPath.fold(sequenceOf(solution)) {
                solutions, path ->
            path.eval(solutions, graph)
        }
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> = compiledPath.fold(emptySet()) { v, p -> v + p.getVariables() }
}
