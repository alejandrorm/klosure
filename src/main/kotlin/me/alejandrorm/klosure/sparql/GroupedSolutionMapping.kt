package me.alejandrorm.klosure.sparql

class GroupedSolutionMapping(
    val boundVariables: SolutionMapping,
    val groups: Sequence<SolutionMapping>
)
