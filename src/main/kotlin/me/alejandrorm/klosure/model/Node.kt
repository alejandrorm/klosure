package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI
import java.util.concurrent.ConcurrentHashMap

open class Node(val id: NodeId) {
    private val incomingEdges: ConcurrentHashMap<IRI, MutableSet<PredicateNode>> = ConcurrentHashMap()

    private val outgoingEdges: ConcurrentHashMap<IRI, MutableSet<PredicateNode>> = ConcurrentHashMap()

    fun addIncomingEdge(node: PredicateNode) {
        incomingEdges.computeIfAbsent((node.id as TripleId).predicate)
          { ConcurrentHashMap.newKeySet<PredicateNode>() as MutableSet<PredicateNode> }
        incomingEdges[node.id.predicate]!!.add(node)
    }

    fun addOutgoingEdge(node: PredicateNode) {
        outgoingEdges.computeIfAbsent((node.id as TripleId).predicate)
          {  ConcurrentHashMap.newKeySet<PredicateNode>() as MutableSet<PredicateNode> }
        outgoingEdges[node.id.predicate]!!.add(node)
    }

    fun getIncomingEdges(): Iterator<PredicateNode> {
        return incomingEdges.values.flatten().iterator()
    }

    fun getOutgoingEdges(): Iterator<PredicateNode> {
        return outgoingEdges.values.flatten().iterator()
    }

    fun getIncomingEdges(predicate: IRI): Iterator<PredicateNode> {
        return incomingEdges[predicate]?.iterator() ?: emptyList<PredicateNode>().iterator()
    }

    fun getOutgoingEdges(predicate: IRI): Iterator<PredicateNode> {
        return outgoingEdges[predicate]?.iterator() ?: emptyList<PredicateNode>().iterator()
    }
}
