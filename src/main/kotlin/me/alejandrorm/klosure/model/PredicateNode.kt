package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI

class PredicateNode(id: TripleId) : Node(id) {
    constructor(subject: NodeId, iri: IRI, rdfObject: NodeId) : this(TripleId(subject, iri, rdfObject))
    constructor(subject: Node, iri: IRI, rdfObject: Node) : this(subject.id, iri, rdfObject.id)

    override fun equals(other: Any?): Boolean {
        return other is Node && other.id == id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        val (subject, verb, rdfObject) = id as TripleId
        return "$subject <$verb> $rdfObject ."
    }
}