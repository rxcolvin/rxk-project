package com.rxk.oauth_helper


data class QueryRes<T, M>(val data: Collection<T>, val stats: M) {}

data class OneRes<T, M>(val dataum: T, val stats: M)


interface QueryDef<T> {
}

data class ByIdQueryDef<K, T, C>(val id: K, val ctx: C) : QueryDef<T> {}

class NotFoundException() : RuntimeException() {
}

class NotUniqueIdError() : RuntimeException() {
}

interface QueryDao<K, T, C, M> {
    fun unique(ctx: C, id: K): OneRes<T, M> = queryOne(ctx, ByIdQueryDef<K, T, C>(id, ctx))
    fun query(ctx: C, queryDef: QueryDef<T>): QueryRes<T, M>
    fun queryOne(ctx: C, queryDef: QueryDef<T>): OneRes<T, M> {
        val res = query(ctx, queryDef)
        return when (res.data.size) {
            1 -> OneRes(res.data.iterator().next(), res.stats)
            0 -> throw NotFoundException()
            else -> throw  NotUniqueIdError()
        }
    }
}







