package me.alejandrorm.klosure.sparql.algebra

import me.alejandrorm.klosure.model.Graph
import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.TripleId
import me.alejandrorm.klosure.sparql.SolutionMapping
import me.alejandrorm.klosure.sparql.Variable

class BasicTriplePattern(val subject: TermOrVariable, val predicate: TermOrVariable, val obj: TermOrVariable) : TriplePattern {

    private val terms = arrayOf(subject, predicate, obj)

    override fun getVariables(): Set<Variable> {
        return terms.filterIsInstance<TermOrVariable.VariableTerm>().map { it.variable }.toSet()
    }

    override fun eval(solutions: Iterable<SolutionMapping>, graph: Graph): Iterable<SolutionMapping> {
        return solutions.flatMap { eval(it, graph) }
    }

    override fun eval(solution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        val resolvedTriple = getKnowns(solution)
        return when (resolvedTriple.type) {
            PatternKnowns.SUB_PRE_OBJ -> getSPO(solution, resolvedTriple, graph)
            PatternKnowns.SUB_PRE -> getSP(solution, resolvedTriple, graph)
            PatternKnowns.SUB -> getS(solution, resolvedTriple, graph)
            PatternKnowns.PRE_OBJ -> getPO(solution, resolvedTriple, graph)
            PatternKnowns.PRE -> getP(solution, resolvedTriple, graph)
            PatternKnowns.OBJ -> getO(solution, resolvedTriple, graph)
            PatternKnowns.SUB_OBJ -> getSO(solution, resolvedTriple, graph)
            PatternKnowns.NONE -> getNone(solution, graph)
        }
    }

    private fun getKnowns(currentSolution: SolutionMapping): ResolvedTriple {
        var mask = 0
        val s = terms[0].resolve(currentSolution)
        if (s.isBound()) mask += 4
        val p = terms[1].resolve(currentSolution)
        if (p.isBound()) mask += 2
        val o = terms[2].resolve(currentSolution)
        if (o.isBound()) mask += 1

        return ResolvedTriple(s, p, o, PatternKnowns.values()[mask])
    }

    private fun getSPO(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concretePre = (triple.predicate.getTerm() as IriId).iri
        val concreteSub = triple.subject.getTerm()
        val concreteObj = triple.obj.getTerm()

        val tripleId = TripleId(concreteSub, concretePre, concreteObj)
        return if (graph.getNode(tripleId) != null) {
            listOf(currentSolution)
        } else {
            emptyList()
        }
    }

    private fun getSP(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concreteSub = triple.subject.getTerm()
        val concretePre = (triple.predicate.getTerm() as IriId).iri

        val it = graph.getNode(concreteSub)?.getOutgoingEdges(concretePre)
        it ?: return emptyList()
        return it.asSequence().map { edge ->
            currentSolution.bind((obj as TermOrVariable.VariableTerm).variable, (edge.id as TripleId).rdfObject)
        }.asIterable()
    }

    private fun getS(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concreteSub = triple.subject.getTerm()

        val it = graph.getNode(concreteSub)?.getOutgoingEdges()
        it ?: return emptyList()

        val objVariable = (obj as TermOrVariable.VariableTerm).variable
        val predicateVariable = (predicate as TermOrVariable.VariableTerm).variable

        return if (objVariable == predicateVariable) {
            it.asSequence().filter { edge ->
                val tripleId = edge.id as TripleId
                tripleId.rdfObject == IriId(tripleId.predicate)
            }.map {
                currentSolution.bind(objVariable, (it.id as TripleId).rdfObject)
            }.asIterable()
        } else {
            it.asSequence().map { edge ->
                val tripleId = edge.id as TripleId
                currentSolution.bind(objVariable, tripleId.rdfObject)
                    .bind(predicateVariable, IriId(tripleId.predicate))
            }.asIterable()
        }
    }

    private fun getPO(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concretePredicate = (triple.predicate.getTerm() as IriId).iri
        val concreteObj = triple.obj.getTerm()

        val it = graph.getNode(concreteObj)?.getIncomingEdges(concretePredicate)
        it ?: return emptyList()
        return it.asSequence().map { edge ->
            currentSolution.bind(
                (subject as TermOrVariable.VariableTerm).variable,
                (edge.id as TripleId).subject
            )
        }.asIterable()
    }

    private fun getP(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concretePre = (triple.predicate.getTerm() as IriId).iri

        val it = graph.getPredicateNodes(concretePre)

        val objVariable = (obj as TermOrVariable.VariableTerm).variable
        val subjectVariable = (subject as TermOrVariable.VariableTerm).variable

        return if (objVariable == subjectVariable) {
            it.asSequence().filter { edge ->
                val tripleId = edge.id as TripleId
                tripleId.rdfObject == tripleId.subject
            }.map {
                currentSolution.bind(objVariable, (it.id as TripleId).rdfObject)
            }.asIterable()
        } else {
            it.map { edge ->
                currentSolution.bind(
                    subjectVariable,
                    (edge.id as TripleId).subject
                )
                    .bind(
                        objVariable,
                        edge.id.rdfObject
                    )
            }.asIterable()
        }
    }

    private fun getO(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concreteObj = triple.obj.getTerm()
        val it = graph.getNode(concreteObj)?.getIncomingEdges()
        it ?: return emptyList()

        val subjectVariable = (subject as TermOrVariable.VariableTerm).variable
        val predicateVariable = (predicate as TermOrVariable.VariableTerm).variable

        return if (subjectVariable == predicateVariable) {
            it.asSequence().filter { edge ->
                val tripleId = edge.id as TripleId
                tripleId.subject == IriId(tripleId.predicate)
            }.map {
                currentSolution.bind(subjectVariable, (it.id as TripleId).subject)
            }.asIterable()
        } else {
            it.asSequence().map { edge ->
                val tripleId = edge.id as TripleId
                currentSolution.bind(subjectVariable, tripleId.subject)
                    .bind(predicateVariable, IriId(tripleId.predicate))
            }.asIterable()
        }
    }

    private fun getSO(currentSolution: SolutionMapping, triple: ResolvedTriple, graph: Graph): Iterable<SolutionMapping> {
        val concreteSubject = triple.subject.getTerm()
        val concreteObj = triple.obj.getTerm()
        val it = graph.getNode(concreteSubject)?.getOutgoingEdges()
        it ?: return emptyList()
        return it.asSequence().filter { predicateNode ->
            (predicateNode.id as TripleId).rdfObject == concreteObj
        }.map { edge ->
            currentSolution.bind(
                (predicate as TermOrVariable.VariableTerm).variable,
                IriId((edge.id as TripleId).predicate)
            )
        }.asIterable()
    }

    private fun getNone(currentSolution: SolutionMapping, graph: Graph): Iterable<SolutionMapping> {
        // FIXME handle repeated variables
        return graph.getAllAssertedTriples().asSequence().map { edge ->
            val tripleId = edge.id as TripleId
            currentSolution.bind(
                (subject as TermOrVariable.VariableTerm).variable,
                tripleId.subject
            )
                .bind(
                    (predicate as TermOrVariable.VariableTerm).variable,
                    IriId(tripleId.predicate)
                )
                .bind(
                    (obj as TermOrVariable.VariableTerm).variable,
                    tripleId.rdfObject
                )
        }.asIterable()
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

    private data class ResolvedTriple(
        val subject: TermOrVariable,
        val predicate: TermOrVariable,
        val obj: TermOrVariable,
        val type: PatternKnowns
    )
}
