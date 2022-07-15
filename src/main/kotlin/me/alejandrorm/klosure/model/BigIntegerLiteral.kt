package me.alejandrorm.klosure.model

import java.math.BigInteger

class BigIntegerLiteral(val value: BigInteger) : NumberLiteral(value.toString())
