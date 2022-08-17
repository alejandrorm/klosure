package me.alejandrorm.klosure.model

import me.alejandrorm.klosure.model.literals.TypedValue
import org.semanticweb.owlapi.model.IRI

sealed class NodeId : Comparable<NodeId>

data class IriId(val iri: IRI) : NodeId() {
    override fun toString(): String = "<$iri>"

    override fun compareTo(other: NodeId): Int = when (other) {
        is BlankId -> 1
        is IriId -> iri.compareTo(other.iri)
        else -> -1
    }
}

data class BlankId(val name: String) : NodeId() {
    override fun toString(): String = "_:$name"

    override fun compareTo(other: NodeId): Int {
        return when (other) {
            is BlankId -> name.compareTo(other.name)
            else -> -1
        }
    }
}

data class LiteralId(
    val literal: String,
    val value: TypedValue
) : NodeId() {
    override fun toString(): String = value.toString()

    override fun equals(other: Any?): Boolean {
        return if (other is LiteralId) {
            value == other.value
        } else false
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun compareTo(other: NodeId): Int {
        return when (other) {
            is BlankId -> 1
            is IriId -> 1
            is LiteralId -> value.compareTo(other.value)
            else -> -1
        }
    }
}

data class TripleId(val subject: NodeId, val predicate: IRI, val obj: NodeId) : NodeId() {
    override fun toString(): String = "<<$subject <$predicate> $obj>>"

    override fun compareTo(other: NodeId): Int {
        return when (other) {
            is BlankId -> 1
            is IriId -> 1
            is LiteralId -> 1
            is TripleId -> {
                val cmp = subject.compareTo(other.subject)
                if (cmp == 0) {
                    val cmp2 = predicate.compareTo(other.predicate)
                    if (cmp2 == 0) {
                        obj.compareTo(other.obj)
                    } else cmp2
                } else {
                    cmp
                }
            }
        }
    }
}
