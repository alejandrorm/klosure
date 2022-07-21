package me.alejandrorm.klosure.sparql.algebra.filters

import me.alejandrorm.klosure.sparql.SolutionMapping

interface Filter {
    fun eval(solution: SolutionMapping): Boolean
}