package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.sparql.Variable

class SolutionSet(
    val variables: List<Variable>,
    val values: List<List<String>>
)
