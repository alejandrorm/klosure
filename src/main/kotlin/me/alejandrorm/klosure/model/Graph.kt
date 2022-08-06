package me.alejandrorm.klosure.model

import me.alejandrorm.klosure.model.literals.RdfConstants
import org.semanticweb.owlapi.model.IRI
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class Graph(val entailment: EntailmentTypes) {
    private val nonTerminalNodes: ConcurrentHashMap<NodeId, Node> = ConcurrentHashMap()
    private val terminalNodes: ConcurrentHashMap<LiteralId, LiteralNode> = ConcurrentHashMap()
    private val predicateNodes: ConcurrentHashMap<IRI, MutableSet<PredicateNode>> = ConcurrentHashMap()

    init {
        when(entailment) {
            EntailmentTypes.RDF -> {
                val type = getOrCreateNode(RdfConstants.TYPE_ID)
                val property = getOrCreateNode(RdfConstants.PROPERTY_ID)

                val predicateId = TripleId(type.id, RdfConstants.TYPE, property.id)
                val predicateNode =
                    nonTerminalNodes.computeIfAbsent(predicateId) { PredicateNode(predicateId) } as PredicateNode
                predicateNodes.computeIfAbsent(RdfConstants.TYPE) { ConcurrentHashMap.newKeySet<PredicateNode>() as MutableSet<PredicateNode> }.add(predicateNode)

                type.addOutgoingEdge(predicateNode)
                property.addIncomingEdge(predicateNode)
            }
            else -> {}
        }
    }


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

    fun getOrPutLiteralNode(id: LiteralId): LiteralNode {
        return terminalNodes.computeIfAbsent(id) { LiteralNode(id) }
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
            when(entailment) {
                EntailmentTypes.RDF -> {
                    val verbNode = getOrCreateNode(IriId(verb))
                    if (verbNode.id != RdfConstants.TYPE_ID) {
                        val propertyNode = getOrCreateNode(RdfConstants.PROPERTY_ID)
                        getOrCreatePredicate(
                            verbNode,
                            RdfConstants.TYPE, propertyNode, true
                        )
                    }
                }
                else -> {}
            }
        }

        return predicateNode
    }

    fun getPredicateNodes(verb: IRI): Set<PredicateNode> {
        return predicateNodes[verb] ?: emptySet()
    }

    fun getAllTriples(): Sequence<PredicateNode> {
        return predicateNodes.values.flatten().asSequence()
    }

    fun getAllAssertedTriples(): Sequence<PredicateNode> {
        return predicateNodes.values.flatten().filter { it.asserted }.asSequence()
    }
}
