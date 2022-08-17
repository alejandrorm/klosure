package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.sparql.SolutionMapping

class GraphGraphPattern(val termOrVariable: TermOrVariable, val operator: AlgebraOperator) : AlgebraOperator {

    fun specialEval(
        graphNames: Sequence<SolutionMapping>,
        solutions: Sequence<SolutionMapping>,
        graphs: Graphs
    ): Sequence<SolutionMapping> {
        return graphNames.flatMap { graphNamesSolution ->
            if (termOrVariable is TermOrVariable.QuotedTriple) {
                throw IllegalArgumentException("QuotedTriple not allowed in GraphGraphPattern")
            }

            val term = termOrVariable.resolve(graphNamesSolution)
            if (term.isBound()) {
                val iri = term.getTerm()
                if (iri is IriId) {
                    val graph = graphs.getGraph(iri.iri)
                    if (graph != null) {
                        operator.eval(solutions, graph, graphs)
                    } else {
                        emptySequence()
                    }
                } else {
                    throw IllegalArgumentException("The term $term is not an IRI")
                }
            } else {
                graphs.getAllGraphs().flatMap { graphEntry ->
                    val newSolutions =
                        solutions.map { it.bind((term as TermOrVariable.VariableTerm).variable, IriId(graphEntry.iri)) }
                    operator.eval(newSolutions, graphEntry.graph, graphs)
                }
            }
        }
    }

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        activeGraph: Graph,
        graphs: Graphs
    ): Sequence<SolutionMapping> {
        return specialEval(solutions, solutions, graphs)
    }

    override fun hasFilter(): Boolean {
        return operator.hasFilter()
    }
}
