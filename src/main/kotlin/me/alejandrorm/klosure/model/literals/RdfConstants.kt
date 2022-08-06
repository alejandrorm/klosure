package me.alejandrorm.klosure.model.literals

import me.alejandrorm.klosure.model.IriId
import me.alejandrorm.klosure.model.LiteralId
import org.semanticweb.owlapi.model.IRI

object RdfConstants {
    val RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns"
    val RDFS = "http://www.w3.org/2000/01/rdf-schema"
    val OWL = "http://www.w3.org/2002/07/owl"
    val XSD = "http://www.w3.org/2001/XMLSchema"

    val TYPE = IRI.create("$RDF#type")
    val SUBJECT = IRI.create("$RDF#subject")
    val PREDICATE = IRI.create("$RDF#predicate")
    val OBJECT = IRI.create("$RDF#object")
    val PROPERTY = IRI.create("$RDF#Property")

    val TYPE_ID = IriId(TYPE)
    val SUBJECT_ID = IriId(SUBJECT)
    val PREDICATE_ID = IriId(PREDICATE)
    val OBJECT_ID = IriId(OBJECT)
    val PROPERTY_ID = IriId(PROPERTY)
}