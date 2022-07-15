package me.alejandrorm.klosure.model

sealed class BooleanLiteral(val value: Boolean) : LiteralNode(LiteralId(value.toString(), null))

object TrueLiteral : BooleanLiteral(true)

object FalseLiteral : BooleanLiteral(false)
