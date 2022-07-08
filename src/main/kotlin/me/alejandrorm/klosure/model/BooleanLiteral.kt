package me.alejandrorm.klosure.model

sealed class BooleanLiteral(val value: Boolean): LiteralNode(LiteralId(value.toString()))

object TrueLiteral: BooleanLiteral(true)

object FalseLiteral: BooleanLiteral(false)