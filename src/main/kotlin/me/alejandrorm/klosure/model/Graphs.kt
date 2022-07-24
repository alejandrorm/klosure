package me.alejandrorm.klosure.model

interface Graphs {
    fun getGraph(name: String): Graph
    fun createGraph(name: String)
}
