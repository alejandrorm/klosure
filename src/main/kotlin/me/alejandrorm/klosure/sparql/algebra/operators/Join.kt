package me.alejandrorm.klosure.sparql.algebra.operators

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.algebra.filters.getEffectiveBooleanValue

class Join(val operators: List<AlgebraOperator>) : AlgebraOperator {

    private val operatorsReordered: List<AlgebraOperator> = operators.filter { it !is Filter } +
            operators.filterIsInstance<Filter>()

//    private val simpleJoin: Boolean = operatorsReordered.all { it is Filter || !it.hasFilter() }

    override fun toString(): String {
        return "Join(${operators.joinToString(", ")})"
    }

    override fun eval(
        solutions: Sequence<SolutionMapping>,
        graph: Graph
    ): Sequence<SolutionMapping> {
//        if (simpleJoin) {
            return operatorsReordered.fold(solutions) { acc, op ->
                topLevelJoin(acc, op, graph)
            }
//        }
//        } else {
//            var sols = solutions.toList()
//            operators.forEach { operator ->
//                val l2 = operator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph)
//                val newSolutions = mutableListOf<SolutionMapping>()
//                l2.forEach { sol1 ->
//                    sols.forEach { sol2 ->
//                        if (sol1.isCompatible(sol2)) {
//                            newSolutions.add(sol1.merge(sol2))
//                        }
//                    }
//                }
//                sols = newSolutions
//            }
//            return sols.asSequence()
//        }
    }

    private fun topLevelJoin(l1: Sequence<SolutionMapping>,
                             operator: AlgebraOperator,
                             graph: Graph
    ): Sequence<SolutionMapping> {
        return if (operator is Filter || operator is LeftJoin || !operator.hasFilter()) {
            operator.eval(l1, graph)
        } else {
            join(l1, operator, graph)
        }
    }

    private fun join(
        l1: Sequence<SolutionMapping>,
        operator: AlgebraOperator,
        graph: Graph
    ): Sequence<SolutionMapping> = sequence {
        val l2 = operator.eval(sequenceOf(SolutionMapping.EmptySolutionMapping), graph).toList()

        for (solution1 in l1) {
            for (solution2 in l2) {
                if (solution1.isCompatible(solution2)) {
                    yield(solution1.merge(solution2))
                }
            }
        }
    }

    override fun hasFilter(): Boolean = operators.any { it.hasFilter() }
}
