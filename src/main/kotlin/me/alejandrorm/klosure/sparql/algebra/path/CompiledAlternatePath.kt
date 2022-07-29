package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class CompiledAlternatePath(val paths: List<CompiledPath>) : CompiledPath {

    override fun toString(): String {
        return "CompiledAlternatePath(${paths.joinToString(", ")})"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
        return paths.fold(emptySequence()) { acc, path ->
            acc.plus(path.eval(solution, graph))
        }
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> =
        paths.fold(setOf()) { acc, path ->
            acc.plus(path.getVariables())
        }
}
