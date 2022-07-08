package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Graph {
    private val nonTerminalNodes: ConcurrentHashMap<NodeId, Node> = ConcurrentHashMap();
    private val terminalNodes: ConcurrentHashMap<LiteralId, LiteralNode> = ConcurrentHashMap();
    private val predicateNodes: ConcurrentHashMap<IRI, MutableSet<PredicateNode>> = ConcurrentHashMap();

    fun getNonTerminalNode(id: NodeId): Node? {
        return nonTerminalNodes[id]
    }

    fun getTerminalNode(id: LiteralId): LiteralNode? {
        return terminalNodes[id]
    }

    fun getOrCreateNode(id: NodeId): Node {
        return nonTerminalNodes.computeIfAbsent(id) { Node(id) }
    }

    fun addNode(node: Node) {
        nonTerminalNodes[node.id] = node
    }

    fun getOrPutLiteralNode(node: LiteralNode): LiteralNode {
        return terminalNodes.computeIfAbsent(node.nodeId) { node }
    }

    fun generateAnonId(): NodeId {
        return IriId(IRI.create("anon_${UUID.randomUUID()}"))
    }

    fun getNewBlankNode(): Node {
        return getOrCreateNode(generateAnonId())
    }

    fun getOrCreatePredicate(subject: Node, verb: IRI, rdfObject: Node): PredicateNode {
        val predicateId = TripleId(subject.id, verb, rdfObject.id)
        val predicateNode =
            nonTerminalNodes.computeIfAbsent(predicateId) { PredicateNode(predicateId) } as PredicateNode
        predicateNodes.computeIfAbsent(verb)
            { ConcurrentHashMap.newKeySet<PredicateNode>() as MutableSet<PredicateNode> }.add(predicateNode)

        subject.addIncomingEdge(predicateNode)
        rdfObject.addOutgoingEdge(predicateNode)

        return predicateNode
    }

    fun getPredicateNodes(verb: IRI): Set<PredicateNode> {
        return predicateNodes[verb] ?: emptySet()
    }
}