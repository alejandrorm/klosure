package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI

sealed class NodeId

data class IriId(val iri: IRI) : NodeId() {
    override fun toString(): String = "<$iri>"
}

data class BlankId(val name: String) : NodeId() {
    override fun toString(): String = "_:$name"
}

// TODO escape quotes
data class LiteralId(val literal: String, val type: IRI?) : NodeId() {
    override fun toString(): String = if (type != null) "\"$literal\"^^<$type>" else "\"$literal\""
}

data class TripleId(val subject: NodeId, val predicate: IRI, val obj: NodeId) : NodeId() {
    override fun toString(): String = "<<$subject <$predicate> $obj>>"
}
