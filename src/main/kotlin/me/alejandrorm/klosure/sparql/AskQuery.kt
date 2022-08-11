package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.algebra.operators.AlgebraOperator

class AskQuery(val algebraOperator: AlgebraOperator) : Query {
    override fun toString(): String {
        return "ASK($algebraOperator)"
    }
    override fun eval(graph: Graph): QueryResult {
        return AskQueryResult(algebraOperator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph).any())
    }
}