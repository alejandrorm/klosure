package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.Graphs

interface Query {
    fun eval(graphs: Graphs): QueryResult
}
