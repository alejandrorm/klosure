package me.alejandrorm.klosure.sparql

sealed class QueryResult

data class SelectQueryResult(val results: Sequence<SolutionMapping>) : QueryResult()

data class AskQueryResult(val result: Boolean) : QueryResult()
