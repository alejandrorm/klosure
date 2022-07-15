package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Graph {
    private val nonTerminalNodes: ConcurrentHashMap<NodeId, Node> = ConcurrentHashMap()
    private val terminalNodes: ConcurrentHashMap<LiteralId, LiteralNode> = ConcurrentHashMap()
    private val predicateNodes: ConcurrentHashMap<IRI, MutableSet<PredicateNode>> = ConcurrentHashMap()

    fun getNode(nodeId: NodeId): Node? {
        return if (nodeId is LiteralId) {
            terminalNodes[nodeId]
        } else {
            nonTerminalNodes[nodeId]
        }
    }

    fun getNonTerminalNode(id: NodeId): Node? {
        return nonTerminalNodes[id]
    }

    fun getTerminalNode(id: LiteralId): LiteralNode? {
        return terminalNodes[id]
    }

    fun getOrCreateNode(id: NodeId): Node {
        return nonTerminalNodes.computeIfAbsent(id) { Node(id) }
    }

    fun addListNode(first: Node, rest: Node): Node {
        val listNode = ListNode(first, rest, generateAnonId())
        nonTerminalNodes[listNode.id] = listNode
        getOrCreatePredicate(listNode, IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#first"), first, true)
        getOrCreatePredicate(listNode, IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest"), rest, true)

        return listNode
    }

    // TODO: should receive a LiteralId. LiteralNode constructors should be package private.
    fun getOrPutLiteralNode(node: LiteralNode): LiteralNode {
        return terminalNodes.computeIfAbsent(node.nodeId) { node }
    }

    fun generateAnonId(): NodeId {
        return BlankId(UUID.randomUUID().toString())
    }

    fun getNewBlankNode(): Node {
        return getOrCreateNode(generateAnonId())
    }

    fun getOrCreatePredicate(subject: Node, verb: IRI, rdfObject: Node, assert: Boolean): PredicateNode {
        val predicateId = TripleId(subject.id, verb, rdfObject.id)
        val predicateNode =
            nonTerminalNodes.computeIfAbsent(predicateId) { PredicateNode(predicateId) } as PredicateNode
        predicateNodes.computeIfAbsent(verb) { ConcurrentHashMap.newKeySet<PredicateNode>() as MutableSet<PredicateNode> }.add(predicateNode)

        subject.addOutgoingEdge(predicateNode)
        rdfObject.addIncomingEdge(predicateNode)

        if (assert) {
            predicateNode.asserted = true
        }

        return predicateNode
    }

    fun getPredicateNodes(verb: IRI): Set<PredicateNode> {
        return predicateNodes[verb] ?: emptySet()
    }

    fun getAllTriples(): Iterator<PredicateNode> {
        return predicateNodes.values.flatten().iterator()
    }

    fun getAllAssertedTriples(): Iterator<PredicateNode> {
        return predicateNodes.values.flatten().filter { it.asserted }.iterator()
    }
}
