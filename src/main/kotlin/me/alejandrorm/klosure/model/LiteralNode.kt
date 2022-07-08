package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI

abstract class LiteralNode(val nodeId :LiteralId): Node(nodeId) {
    companion object {
        @JvmStatic
        fun create(value: String, lang: String?, type: IRI?): LiteralNode {
            //TODO: check type
            return StringLiteral(value, lang)
        }
    }
}