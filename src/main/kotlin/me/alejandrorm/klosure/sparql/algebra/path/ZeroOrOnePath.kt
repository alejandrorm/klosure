package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

class ZeroOrOnePath(val path: Path) : Path {
    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath {
        return CompiledZeroOrOnePath(head, path.compile(head, tail), tail)
    }

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solution: SolutionMapping,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return compile(head, tail).eval(solution, graph)
    }
}
