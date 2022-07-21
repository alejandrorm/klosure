package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.TermOrVariable

class ZeroOrOnePath(val path: Path) : Path {
    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath {
        return CompiledZeroOrOnePath(head, path.compile(head, tail), tail)
    }

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solutionMapping: SolutionMapping,
        graph: Graph
    ): Iterable<SolutionMapping> {
        return if (head.resolve(solutionMapping) == tail.resolve(solutionMapping)) {
            listOf(solutionMapping)
        } else {
            path.eval(head, tail, solutionMapping, graph)
        }
    }
}