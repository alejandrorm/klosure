package me.alejandrorm.klosure.model

import org.semanticweb.owlapi.model.IRI

class TypedLiteral(value: String, type: IRI): LiteralNode(LiteralId(value, type))