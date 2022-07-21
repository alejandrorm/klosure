package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.BasicTriplePattern
import me.alejandrorm.klosure.sparql.algebra.TermOrVariable
import org.semanticweb.owlapi.model.IRI

class CompiledIriPath(
    val head: TermOrVariable,
    val iri: IRI,
    val tail: TermOrVariable) : CompiledPath {

    private val triple = BasicTriplePattern(head, TermOrVariable.NodeOrIriTerm(IriId(iri)), tail)

    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> =
        triple.eval(solution, graph)

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> =
        triple.eval(solutions, graph)

    override fun getVariables(): Set<Variable> = triple.getVariables()
}