package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

class NegatedPath(val path: Path) : Path {
    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath {
        TODO("Not yet implemented")
    }

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solutionMapping: SolutionMapping,
        graph: Graph
    ): Iterable<SolutionMapping> {
        TODO("Not yet implemented")
    }
}
