package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.algebra.operators.AlgebraOperator
import me.alejandrorm.klosure.sparql.algebra.operators.SolutionModifier

class SelectQuery(val algebraOperator: AlgebraOperator, val solutionModifier: SolutionModifier) : Query {
    override fun toString(): String {
        return "SELECT($algebraOperator)"
    }
    override fun eval(graph: Graph): QueryResult {
        val basicResult = algebraOperator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph)
        //val modifiedResult = solutionModifier.limit?.let { it.eval(basicResult, graph) } ?: basicResult
        return SelectQueryResult(solutionModifier.eval(basicResult, graph))
    }
}
