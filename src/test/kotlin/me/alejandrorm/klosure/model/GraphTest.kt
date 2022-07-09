package me.alejandrorm.klosure.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.semanticweb.owlapi.model.IRI

class GraphTest {

    // basic add node
    @Test
    fun addNode() {
        val graph = Graph()

        // add node by iri
        val nodeId = IriId(IRI.create("http://example.org/node"))
        val iriNode = graph.getOrCreateNode(nodeId)

        Assertions.assertEquals(nodeId, iriNode.id)
        Assertions.assertSame(iriNode, graph.getNode(iriNode.id))
        Assertions.assertSame(iriNode, graph.getOrCreateNode(nodeId))


        val nodeId2 = IriId(IRI.create("http://example.org/node2"))
        val iriNode2 = graph.getOrCreateNode(nodeId2)

        Assertions.assertEquals(nodeId2, iriNode2.id)
        Assertions.assertSame(iriNode2, graph.getNode(iriNode2.id))
        Assertions.assertSame(iriNode2, graph.getOrCreateNode(nodeId2))
        Assertions.assertSame(iriNode, graph.getOrCreateNode(nodeId))

        // add blank node by name
        val blankNode = graph.getOrCreateNode(BlankId("blank"))

        Assertions.assertEquals(BlankId("blank"), blankNode.id)
        Assertions.assertSame(blankNode, graph.getOrCreateNode(BlankId("blank")))
        Assertions.assertSame(blankNode, graph.getNode(BlankId("blank")))

        // add blank node
        val blankNode2 = graph.getNewBlankNode()
        val blankNode3 = graph.getNewBlankNode()

        Assertions.assertNotSame(blankNode2, blankNode3)
        Assertions.assertNotEquals(blankNode2.id, blankNode3.id)
        Assertions.assertSame(blankNode2, graph.getOrCreateNode(blankNode2.id))
        Assertions.assertSame(blankNode2, graph.getNode(blankNode2.id))
        Assertions.assertSame(blankNode3, graph.getOrCreateNode(blankNode3.id))
        Assertions.assertSame(blankNode3, graph.getNode(blankNode3.id))
    }

    // basic add edge

    // multi thread add node and edge

    // consistency check
}