package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.algebra.operators.AlgebraOperator
import org.semanticweb.owlapi.model.IRI

class AskQuery(val algebraOperator: AlgebraOperator,
              val defaultGraph: IRI?) : Query {
    override fun toString(): String {
        return "ASK($algebraOperator)"
    }
    override fun eval(graphs: Graphs): QueryResult {
        defaultGraph?.let {
            graphs.setDefaultGraph(graphs.createGraph(it))
        }

        return AskQueryResult(algebraOperator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping),
            graphs.getDefaultGraph(), graphs).any())
    }
}