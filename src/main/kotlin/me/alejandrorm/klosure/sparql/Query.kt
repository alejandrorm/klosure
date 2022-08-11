package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graph

interface Query {
    fun eval(graph: Graph): QueryResult
}
