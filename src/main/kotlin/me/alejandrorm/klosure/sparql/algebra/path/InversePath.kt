package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

class InversePath(val path: Path) : Path {
    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath =
        CompiledInversePath(path.compile(tail, head))

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solutionMapping: SolutionMapping,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return path.eval(tail, head, solutionMapping, graph)
    }
}
