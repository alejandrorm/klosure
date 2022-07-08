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
}
