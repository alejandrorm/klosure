package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable
import java.lang.IllegalStateException

class BasicTriplePattern(val subject: TermOrVariable, val predicate: TermOrVariable, val obj: TermOrVariable) : TriplePattern {

    private val terms = arrayOf(subject, predicate, obj)

    override fun getVariables(): Set<Variable> {
        return terms.filterIsInstance<TermOrVariable.VariableTerm>().map { it.variable }.toSet()
    }

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        return when (getKnowns(solution)) {
            PatternKnowns.SUB_PRE_OBJ -> getSPO(solution, graph)
            PatternKnowns.SUB_PRE -> getSP(solution, graph)
            PatternKnowns.SUB -> getS(solution, graph)
            PatternKnowns.PRE_OBJ -> getPO(solution, graph)
            PatternKnowns.PRE -> getP(solution, graph)
            PatternKnowns.OBJ -> getO(solution, graph)
            PatternKnowns.SUB_OBJ -> getSO(solution, graph)
            PatternKnowns.NONE -> getNone(solution, graph)
        }
    }

    private fun getKnowns(currentSolution: SolutionMapping): PatternKnowns {
        var mask = 0
        for (i in terms.indices) {
            // TODO return the bound values too here so we don't have to look them up again later
            if (terms[i].resolve(currentSolution).isBound()) {
                mask += (1 shl (2 - i))
            }
        }
        // TODO benchmark if it is worthy to roll out the loop
//        if (triple.subject is TermOrVariable.NodeTerm ||
//            (triple.subject as TermOrVariable.VariableTerm).variable.isBound) mask += 4
//        if (triple.predicate is TermOrVariable.NodeTerm ||
//            (triple.predicate as TermOrVariable.VariableTerm).variable.isBound) mask += 2
//        if (triple.obj is TermOrVariable.NodeTerm ||
//            (triple.obj as TermOrVariable.VariableTerm).variable.isBound) mask += 1

        return PatternKnowns.values()[mask]
    }

    private fun getSPO(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        val concretePre = predicate.resolve(currentSolution)
        val concreteSub = subject.resolve(currentSolution)
        val concreteObj = obj.resolve(currentSolution)

        // TODO get id of triple
//        return if (graph.getNode(TripleId(concreteObj as ))getPredicateNodes(concretePre).contains()
//            .contains(pre)
//        ) {
//            listOf(currentSolution)
//        } else {
//            emptyList()
//        }
        TODO()
    }

    private fun getSP(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        val concreteSub = subject.resolve(currentSolution)
        val concretePre = predicate.resolve(currentSolution)

        val subId = when(concreteSub) {
            is TermOrVariable.IriTerm -> concreteSub.iri
            is TermOrVariable.BlankTerm -> concreteSub.blankId
            is TermOrVariable.TripleTerm -> concreteSub.tripleId
            else -> throw IllegalStateException()
        }

        val it = graph.getNode(subId)?.getOutgoingEdges((concretePre as TermOrVariable.IriTerm).iri.iri)
        it ?: return emptyList()
        return it.asSequence().map { edge ->
            currentSolution.bind((obj as TermOrVariable.VariableTerm).variable, (graph.getNode((edge.id as TripleId).rdfObject)!!))
        }.asIterable()
    }

    private fun getS(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        TODO()
//        val sub =
//            when (subject) {
//                is TermOrVariable.NodeTerm -> subject.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[subject.variable]!!
//            }
//        val it = graph.getNode(sub.id)?.getOutgoingEdges()
//        it ?: return emptyList()
//        return it.asSequence().map { edge ->
//            // FIXME predicate and object could be the same variable
//            val tripleId = edge.id as TripleId
//            currentSolution.bind((obj as TermOrVariable.VariableTerm).variable, (graph.getNode(tripleId.rdfObject)!!))
//                .bind((predicate as TermOrVariable.VariableTerm).variable, edge)
//        }.asIterable()
    }

    private fun getPO(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        TODO()
//        val preIri =
//            when (predicate) {
//                // TODO make this more readable by moving this stuff to TermOrVariable
//                is TermOrVariable.NodeTerm -> (predicate.node.id as TripleId).predicate
//                is TermOrVariable.VariableTerm -> (currentSolution.boundVariables[predicate.variable]!!.id as TripleId).predicate
//            }
//        val obj =
//            when (obj) {
//                is TermOrVariable.NodeTerm -> obj.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[obj.variable]!!
//            }
//
//        val it = graph.getNode(obj.id)?.getIncomingEdges(preIri)
//        it ?: return emptyList()
//        return it.asSequence().map { edge ->
//            currentSolution.bind(
//                (subject as TermOrVariable.VariableTerm).variable,
//                (graph.getNode((edge.id as TripleId).subject)!!)
//            )
//        }.asIterable()
    }

    private fun getP(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        TODO()
//        val preIri =
//            when (predicate) {
//                // TODO make this more readable by moving this stuff to TermOrVariable
//                is TermOrVariable.NodeTerm -> (predicate.node.id as TripleId).predicate
//                is TermOrVariable.VariableTerm -> (currentSolution.boundVariables[predicate.variable]!!.id as TripleId).predicate
//            }
//
//        val it = graph.getPredicateNodes(preIri)
//        return it.map { edge ->
//            // FIXME subject and object could be the same variable and then need to be the same node
//            currentSolution.bind(
//                (subject as TermOrVariable.VariableTerm).variable,
//                (graph.getNode((edge.id as TripleId).subject)!!)
//            )
//                .bind(
//                    (obj as TermOrVariable.VariableTerm).variable,
//                    (graph.getNode(edge.id.rdfObject)!!)
//                )
//        }.asIterable()
    }

    private fun getO(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
TODO()
//        val obj =
//            when (obj) {
//                is TermOrVariable.NodeTerm -> obj.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[obj.variable]!!
//            }
//        val it = graph.getNode(obj.id)?.getIncomingEdges()
//        it ?: return emptyList()
//        return it.asSequence().map { edge ->
//            // FIXME predicate and subject could be the same variable
//            val tripleId = edge.id as TripleId
//            currentSolution.bind((subject as TermOrVariable.VariableTerm).variable, (graph.getNode(tripleId.subject)!!))
//                .bind((predicate as TermOrVariable.VariableTerm).variable, edge)
//        }.asIterable()
    }

    private fun getSO(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
TODO()
//        val sub =
//            when (subject) {
//                is TermOrVariable.NodeTerm -> subject.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[subject.variable]!!
//            }
//        val obj =
//            when (obj) {
//                is TermOrVariable.NodeTerm -> obj.node
//                is TermOrVariable.VariableTerm -> currentSolution.boundVariables[obj.variable]!!
//            }
//        val it = graph.getNode(sub.id)?.getOutgoingEdges()
//        it ?: return emptyList()
//        return it.asSequence().filter { predicateNode ->
//            (predicateNode.id as TripleId).rdfObject == obj
//        }.map { edge ->
//            currentSolution.bind((predicate as TermOrVariable.VariableTerm).variable, edge)
//        }.asIterable()
    }

    private fun getNone(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        TODO()
        // check for on of five cases
        // all variables different, all variables the same, or two of them the same
    }

    private enum class PatternKnowns {
        NONE,
        OBJ,
        PRE,
        PRE_OBJ,
        SUB,
        SUB_OBJ,
        SUB_PRE,
        SUB_PRE_OBJ
    }
}
