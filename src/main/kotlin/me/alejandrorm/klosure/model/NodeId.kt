package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI

sealed class NodeId

data class IriId(val iri: IRI) : NodeId() {
    override fun toString(): String = "<${iri}>"
}

//TODO: remove whitespaces
data class BlankId(val name: String) : NodeId() {
    override fun toString(): String = "_:$name"
}

//TODO include literal type in id
//TODO escape quotes
data class LiteralId(val literal: String) : NodeId() {
    override fun toString(): String = "\"$literal\""
}

data class TripleId(val subject: NodeId, val predicate: IRI, val rdfObject: NodeId) : NodeId() {
    override fun toString(): String = "<<${subject} <${predicate}> ${rdfObject}>>"
}
