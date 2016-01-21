package com.rxk.oauth_helper





class SqlDaoModule<K, T, C, M>(
        handlerResolver: (QueryDef<T>) -> (QueryDef<T>, C) -> QueryRes<T, M>
                     ) {
    val dao: QueryDao<K, T, C, M> = SqlQueryDaoImpl<K, T, C, M>(handlerResolver)
}

private class SqlQueryDaoImpl<K, T, C, M>(
        val handlerResolver: (QueryDef<T>) -> (QueryDef<T>, C) -> QueryRes<T, M>
                                 ) : QueryDao<K, T, C, M> {

    override fun query(ctx: C, queryDef: QueryDef<T>): QueryRes<T, M> =
    //Mucky - use partial function??
        handlerResolver(queryDef)(queryDef, ctx)

}


