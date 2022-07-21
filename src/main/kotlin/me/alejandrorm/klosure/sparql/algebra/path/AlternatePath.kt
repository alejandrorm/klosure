package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.TermOrVariable

class AlternatePath(val path1: Path, val path2: Path): Path {
    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath =
        CompiledAlternatePath(path1.compile(head, tail), path2.compile(head, tail))

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solutionMapping: SolutionMapping,
        graph: Graph
    ): Iterable<SolutionMapping> {
        return path1.eval(head, tail, solutionMapping, graph) + path2.eval(head, tail, solutionMapping, graph)
    }
}