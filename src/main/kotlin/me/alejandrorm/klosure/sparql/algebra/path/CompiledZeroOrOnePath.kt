package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable

class CompiledZeroOrOnePath(
    val head: TermOrVariable,
    val path: CompiledPath,
    val tail: TermOrVariable
) : CompiledPath {

    override fun toString(): String {
        return "CompiledZeroOrOnePath($head, ($path), $tail)"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
        val concreteHead = head.resolve(solution)
        val concreteTail = tail.resolve(solution)
        val s = if (concreteHead == concreteTail) {
            sequenceOf(solution)
        } else if (concreteHead.isBound() && !concreteTail.isBound()) {
            if (concreteTail is TermOrVariable.VariableTerm) {
                sequenceOf(solution.bind(concreteTail.variable, concreteHead.getTerm()))
            } else {
                (concreteTail as TermOrVariable.QuotedTriple).match(solution, concreteHead.getTerm())
                    ?.let { sequenceOf(it) } ?: emptySequence()
            }
        } else if (concreteTail.isBound() && !concreteHead.isBound()) {
            if (concreteHead is TermOrVariable.VariableTerm) {
                sequenceOf(solution.bind(concreteHead.variable, concreteTail.getTerm()))
            } else {
                (concreteHead as TermOrVariable.QuotedTriple).match(solution, concreteTail.getTerm())
                    ?.let { sequenceOf(it) } ?: emptySequence()
            }
        } else if (!concreteHead.isBound() && !concreteTail.isBound()) {
            graph.getAllSubjects().map { subject ->
                solution.bind((concreteHead as TermOrVariable.VariableTerm).variable, subject)
                    .bind((concreteTail as TermOrVariable.VariableTerm).variable, subject)
            } + graph.getAllTerminals().map { subject ->
                solution.bind((concreteHead as TermOrVariable.VariableTerm).variable, subject)
                    .bind((concreteTail as TermOrVariable.VariableTerm).variable, subject)
            }
        } else {
            emptySequence()
        }

        return s + path.eval(solution, graph)
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> {
        TODO("Not yet implemented")
    }
}
