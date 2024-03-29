package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.operators.BasicTriplePattern
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable
import org.semanticweb.owlapi.model.IRI

class IriPath(val iri: IRI) : Path {

    override fun toString(): String {
        return "IriPath($iri)"
    }

    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath =
        CompiledIriPath(head, iri, tail)

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solutionMapping: SolutionMapping,
        graph: Graph
    ): Sequence<SolutionMapping> {
        return BasicTriplePattern(head, TermOrVariable.NodeOrIriTerm(IriId(iri)), tail).eval(solutionMapping, graph)
    }
}
