package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs
import me.alejandrorm.klosure.sparql.algebra.operators.AlgebraOperator
import me.alejandrorm.klosure.sparql.algebra.operators.SolutionModifier
import org.semanticweb.owlapi.model.IRI

class SelectQuery(val algebraOperator: AlgebraOperator,
                  val solutionModifier: SolutionModifier,
                  val defaultGraph: IRI?) : Query {
    override fun toString(): String {
        return "SELECT($algebraOperator)"
    }
    override fun eval(graphs: Graphs): QueryResult {
        defaultGraph?.let {
            graphs.setDefaultGraph(graphs.createGraph(it))
        }
        val basicResult = algebraOperator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graphs.getDefaultGraph(),
        graphs)
        //val modifiedResult = solutionModifier.limit?.let { it.eval(basicResult, graph) } ?: basicResult
        return SelectQueryResult(solutionModifier.eval(basicResult, graphs.getDefaultGraph(),graphs))
    }
}
