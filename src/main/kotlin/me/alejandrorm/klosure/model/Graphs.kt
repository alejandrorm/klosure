package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI

data class GraphsEntry(val iri: IRI, val graph: Graph)

class Graphs(private val entailment: EntailmentTypes) {
    private var default: Graph = Graph(entailment)
    private val graphs = mutableMapOf<IRI, Graph>()

    init {
        graphs[IRI.create("graph:default")] = default
    }

    fun getGraph(name: IRI): Graph? = graphs[name]
    fun createGraph(name: IRI): Graph = graphs.computeIfAbsent(name) { Graph(entailment) }
    fun getDefaultGraph(): Graph = default
    fun setDefaultGraph(graph: Graph) {
        default = graph
    }
    fun getAllGraphs(): Sequence<GraphsEntry> = graphs.entries.asSequence().map { GraphsEntry(it.key, it.value) }
}
