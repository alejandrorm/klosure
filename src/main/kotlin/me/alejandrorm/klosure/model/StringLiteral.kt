package me.alejandrorm.klosure.model

class StringLiteral(value: String, val language: String?) : LiteralNode(LiteralId(value, null))
