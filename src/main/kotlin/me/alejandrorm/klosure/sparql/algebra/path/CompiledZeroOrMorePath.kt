package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable
import me.alejandrorm.klosure.sparql.algebra.operators.TriplePattern

class CompiledZeroOrMorePath(
    val head: TermOrVariable,
    val path: Path,
    val tail: TermOrVariable
) : CompiledPath, TriplePattern {

    override fun toString(): String {
        return "CompiledZeroOrMorePath($head, $path, $tail)"
    }

    private val oneOrMorePath = OneOrMorePath(path).compile(head, tail)

    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
        val s = if (head.resolve(solution) == tail.resolve(solution)) {
            sequenceOf(solution)
        } else {
            emptySequence()
        }

        return s + oneOrMorePath.eval(solution, graph)
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> = TODO() // setOf(head, tail)
}