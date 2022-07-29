package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.algebra.operators.AlgebraOperator

class SelectQuery(val algebraOperator: AlgebraOperator) : Query {
    override fun toString(): String {
        return algebraOperator.toString()
    }
    override fun eval(graph: Graph): QueryResult {
        return QueryResult(algebraOperator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph))
    }
}
