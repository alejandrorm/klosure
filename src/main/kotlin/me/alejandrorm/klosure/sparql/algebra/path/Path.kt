package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.TermOrVariable

interface Path {
    fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath

    fun eval(head: TermOrVariable, tail: TermOrVariable, solutionMapping: SolutionMapping, graph: Graph): Iterable<SolutionMapping>

    fun eval(head: TermOrVariable, tail: TermOrVariable, solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> {
        return solutions.flatMap { eval(head, tail, it, graph) }
    }
}
