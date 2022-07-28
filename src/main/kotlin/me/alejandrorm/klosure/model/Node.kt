package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI
import java.util.concurrent.ConcurrentHashMap

open class Node(val id: NodeId) {
    private val incomingEdges: ConcurrentHashMap<IRI, MutableSet<PredicateNode>> = ConcurrentHashMap()

    private val outgoingEdges: ConcurrentHashMap<IRI, MutableSet<PredicateNode>> = ConcurrentHashMap()

    fun addIncomingEdge(node: PredicateNode) {
        incomingEdges.computeIfAbsent((node.id as TripleId).predicate) { ConcurrentHashMap.newKeySet<PredicateNode>() as MutableSet<PredicateNode> }
        incomingEdges[node.id.predicate]!!.add(node)
    }

    fun addOutgoingEdge(node: PredicateNode) {
        outgoingEdges.computeIfAbsent((node.id as TripleId).predicate) { ConcurrentHashMap.newKeySet<PredicateNode>() as MutableSet<PredicateNode> }
        outgoingEdges[node.id.predicate]!!.add(node)
    }

    fun getIncomingEdges(): Sequence<PredicateNode> {
        return incomingEdges.values.flatten().asSequence()
    }

    fun getOutgoingEdges(): Sequence<PredicateNode> {
        return outgoingEdges.values.flatten().asSequence()
    }

    fun getIncomingEdges(predicate: IRI): Sequence<PredicateNode> {
        return incomingEdges[predicate]?.asSequence() ?: emptyList<PredicateNode>().asSequence()
    }

    fun getOutgoingEdges(predicate: IRI): Sequence<PredicateNode> {
        return outgoingEdges[predicate]?.asSequence() ?: emptyList<PredicateNode>().asSequence()
    }

    override fun toString(): String {
        return "Node($id)"
    }
}
