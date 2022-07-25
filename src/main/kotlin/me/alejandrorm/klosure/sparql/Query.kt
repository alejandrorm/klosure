package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graph

data class QueryResult(val bindings: Sequence<SolutionMapping>)

interface Query {
    fun eval(graph: Graph): QueryResult
}
