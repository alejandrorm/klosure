package me.alejandrorm.klosure.model

import java.math.BigDecimal

class BigDecimalLiteral(val value: BigDecimal) : NumberLiteral(value.toString())