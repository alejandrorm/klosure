package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class CompiledAlternatePath(val path1: CompiledPath, val path2: CompiledPath) : CompiledPath {
    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        return path1.eval(solution, graph) + path2.eval(solution, graph)
    }

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> =
        path1.getVariables() + path2.getVariables()
}
