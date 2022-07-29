package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

class AlternatePath(val paths: List<Path>) : Path {

    override fun toString(): String {
        return "AlternatePath(${paths.joinToString(", ")})"
    }

    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath =
        CompiledAlternatePath(paths.map { it.compile(head, tail) })

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solutionMapping: SolutionMapping,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return paths.fold(emptySequence()) { acc, path ->
            acc + path.eval(head, tail, solutionMapping, graph)
        }
    }
}
