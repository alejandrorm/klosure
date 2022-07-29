package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

class CompiledZeroOrOnePath(
    val head: TermOrVariable,
    val path: CompiledPath,
    val tail: TermOrVariable
) : CompiledPath {

    override fun toString(): String {
        return "CompiledZeroOrOnePath($head, $path, $tail)"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
        val n1 = head.resolve(solution)
        val n2 = head.resolve(solution)

        return if (n1.isBound() && n2.isBound() && n1 == n2) {
            sequenceOf(solution)
        } else {
            path.eval(solution, graph)
        }
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> {
        TODO("Not yet implemented")
    }
}
