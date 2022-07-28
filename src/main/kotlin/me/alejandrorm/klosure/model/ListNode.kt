package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI

object NilListNode : Node(IriId(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil")))

class ListNode(val first: Node, val rest: Node, id: NodeId) : Node(id) {
    companion object {
        @JvmStatic
        fun create(list: List<Node>, graph: Graph): Node {
            val node = if (list.isEmpty()) {
                NilListNode
            } else {
                val first = list.first()
                val rest = create(list.drop(1), graph)
                graph.addListNode(first, rest)
            }
            // TODO: add type rdf:List to the node
            return node
        }
    }
}
