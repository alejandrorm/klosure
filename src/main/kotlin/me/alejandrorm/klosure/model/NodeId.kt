package me.alejandrorm.klosure.model

import me.alejandrorm.klosure.model.literals.TypedValue
import org.semanticweb.owlapi.model.IRI



sealed class NodeId

data class IriId(val iri: IRI) : NodeId() {
    override fun toString(): String = "<$iri>"
}

data class BlankId(val name: String) : NodeId() {
    override fun toString(): String = "_:$name"
}

// FIXME "1."^^xsd:decimal is different from 1.0
data class LiteralId(val literal: String,
                     val value: TypedValue) : NodeId() {
    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        return if (other is LiteralId)
            value == other.value else false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

data class TripleId(val subject: NodeId, val predicate: IRI, val obj: NodeId) : NodeId() {
    override fun toString(): String = "<<$subject <$predicate> $obj>>"
}

