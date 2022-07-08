package me.alejandrorm.klosure.model

class StringLiteral(val value: String, val language: String?) : LiteralNode(LiteralId(value))