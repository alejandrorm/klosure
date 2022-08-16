package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.algebra.operators.AlgebraOperator
import me.alejandrorm.klosure.sparql.algebra.operators.SolutionModifiers
import org.semanticweb.owlapi.model.IRI

class SelectQuery(val algebraOperator: AlgebraOperator,
                  val defaultGraph: IRI?) : Query {
    override fun toString(): String {
        return "SELECT($algebraOperator)"
    }
    override fun eval(graphs: Graphs): QueryResult {
        defaultGraph?.let {
            graphs.setDefaultGraph(graphs.createGraph(it))
        }
        return SelectQueryResult(algebraOperator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graphs.getDefaultGraph(),
            graphs))
    }
}
