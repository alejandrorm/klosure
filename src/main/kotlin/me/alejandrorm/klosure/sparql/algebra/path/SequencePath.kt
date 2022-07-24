package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable
import java.util.*

class SequencePath(val paths: List<Path>) : Path {
    private val intermediateVariables = paths.drop(1).map {
        TermOrVariable.VariableTerm(
            Variable(UUID.randomUUID().toString(), true)
        )
    }

    override fun compile(head: TermOrVariable, tail: TermOrVariable): CompiledPath {
        val compiledPath = listOf(paths[0].compile(head, intermediateVariables.first())) +
            (1 until paths.size - 2).map {
                paths[it].compile(intermediateVariables[it - 1], intermediateVariables[it])
            } + listOf(paths.last().compile(intermediateVariables.last(), tail))

        return CompiledSequencePath(compiledPath)
    }

    override fun eval(
        head: TermOrVariable,
        tail: TermOrVariable,
        solutionMapping: SolutionMapping,
        graph: Graph
    ): Sequence<SolutionMapping> {
        val allVariables = listOf(head) + intermediateVariables + listOf(tail)

        return (0..allVariables.size).fold(sequenceOf(solutionMapping)) {
                solutions, i ->
            paths[i].eval(head, tail, solutions, graph)
        }
    }
}
