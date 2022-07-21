package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class CompiledInversePath(val path: CompiledPath): CompiledPath {
    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> = path.eval(solution, graph)

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> =
        path.eval(solutions, graph)

    override fun getVariables(): Set<Variable> = path.getVariables()
}