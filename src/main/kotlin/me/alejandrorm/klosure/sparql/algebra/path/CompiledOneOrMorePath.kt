package me.alejandrorm.klosure.sparql.algebra.path

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.NodeId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable
import me.alejandrorm.klosure.sparql.algebra.operators.TriplePattern
import java.util.UUID

class CompiledOneOrMorePath(
    val head: TermOrVariable,
    val path: Path,
    val tail: TermOrVariable
) : CompiledPath, TriplePattern {

    override fun toString(): String {
        return "CompiledOneOrMorePath($head, $path, $tail)"
    }

    override fun eval(solution: SolutionMapping, graph: Graph): Sequence<SolutionMapping> {
        val s = path.eval(head, tail, solution, graph)
        //if (s.count() > 0) return s

        val v = TermOrVariable.VariableTerm(Variable(UUID.randomUUID().toString(), true))
        val solutions = path.eval(head, v, sequenceOf(solution), graph)
        return s + solutions.flatMap {
            evalHelper(it, v, graph, setOf(head.resolve(it).getTerm()))
        }
    }

    private fun evalHelper(
        solution: SolutionMapping,
        variable: TermOrVariable.VariableTerm,
        graph: Graph,
        boundValues: Set<NodeId>
    ): Sequence<SolutionMapping> {
        val s = path.eval(variable, tail, solution, graph)
        //if (s.count() > 0) return s

        val v = TermOrVariable.VariableTerm(Variable(UUID.randomUUID().toString(), true))

        val solutions = path.eval(variable, v, solution, graph)
        return s + solutions.flatMap {
            val boundValue = variable.resolve(it).getTerm()
            if (boundValues.contains(boundValue)) emptySequence()
            else evalHelper(it, v, graph, boundValues + boundValue)
        }
    }

    override fun eval(solutions: Sequence<SolutionMapping>, graph: Graph): Sequence<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun getVariables(): Set<Variable> = TODO() // setOf(head, tail)
}
