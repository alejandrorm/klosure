package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI

sealed class NodeId

data class IriId(val iri: IRI) : NodeId()

//TODO include literal type in id
data class LiteralId(val literal: String) : NodeId()

data class TripleId(val subject: NodeId, val predicate: IRI, val rdfObject: NodeId): NodeId()
