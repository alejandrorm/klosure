package me.alejandrorm.klosure.sparql

import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.sparql.algebra.operators.BasicTriplePattern
import me.alejandrorm.klosure.sparql.algebra.operators.TermOrVariable
import me.alejandrorm.klosure.sparql.algebra.operators.TriplePattern
import org.semanticweb.owlapi.model.IRI
import java.util.UUID

class ListTerm(val node: TermOrVariable, val listStructure: List<TriplePattern>) {
    companion object {
        @JvmStatic
        val NILL_TERM = ListTerm(
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil"))),
            emptyList()
        )

        private val FIRST =
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#first")))
        private val REST =
            TermOrVariable.NodeOrIriTerm(IriId(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest")))

        @JvmStatic
        fun create(list: List<TermOrVariable>): ListTerm {
            val node: ListTerm = if (list.isEmpty()) {
                NILL_TERM
            } else {
                val first = list.first()
                val rest = create(list.drop(1))

                val blank = TermOrVariable.VariableTerm(Variable(UUID.randomUUID().toString(), true))

                ListTerm(
                    blank,
                    rest.listStructure +
                        BasicTriplePattern(blank, FIRST, first) +
                        BasicTriplePattern(blank, REST, rest.node)
                )
            }
            return node
        }
    }
}
