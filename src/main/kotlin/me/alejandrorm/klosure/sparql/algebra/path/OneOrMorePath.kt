package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.TermOrVariable

class OneOrMorePath(val path: Path): Path {

    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath {
        return CompiledOneOrMorePath(head, path, tail)
    }

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solutionMapping: SolutionMapping,
        graph: Graph
    ): Iterable<SolutionMapping> {
        return compile(head, tail).eval(solutionMapping, graph)
    }
}