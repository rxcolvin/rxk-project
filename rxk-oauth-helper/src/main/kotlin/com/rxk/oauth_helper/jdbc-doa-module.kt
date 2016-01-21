package com.rxk.oauth_helper

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*
import kotlin.collections.indices

/**
 * Created by richard.colvin on 21/01/2016.
 */
interface JdbcQueryHandlerDelegate<T, C, M> {
    val rowMapper: (ResultSet, Connection) -> T
    val select: String
    val where: String
    val parameters: Array<Any>
    val stats: M
}

fun ResultSet.getYorN(name: String): Boolean = this.getString(name) == "Y"

fun <T> jdbcTemplate(connectionFactory: () -> Connection,
                     sql: String,
                     params: Array<Any>,
                     rowMapper: (ResultSet, Connection) -> T): Collection<T> {
    var connection: Connection? = null
    var st: PreparedStatement? = null
    var rs: ResultSet? = null
    val list = ArrayList<T>()

    try {
        connection = connectionFactory()
        st = connection.prepareStatement(sql)


        for (i in params.indices) {
            val x = params[i]
            val n = i + 1
            when (x) {
                is String -> st?.setString(n, x)
                is Long -> st?.setLong(n, x)
                is Int -> st?.setInt(n, x)
                is java.util.Date -> st?.setDate(n, java.sql.Date(x.time))
                else -> throw RuntimeException("Spit")
            }
        }

        rs = st.executeQuery();
        while (rs.next()) {
            list.add(rowMapper(rs, connection))
        }

        return list
    } finally {
        connection?.close()
        rs?.close()
        st?.close()
    }
}


/**
 * A class that uses a very basic abstraction over Jdbc to implementent query handlers
 */
private class JdbcQueryHandlerImpl<T, C, M>(
        val connectionFactory: () -> Connection,
        val df: (C, QueryDef<T>) -> JdbcQueryHandlerDelegate<T, C, M>) {

    operator fun invoke(queryDef: QueryDef<T>, ctx: C): QueryRes<T, M> {

        val d = df(ctx, queryDef)
        val sql = "SELECT " + d.select + " WHERE " + d.where;
        val list = jdbcTemplate(connectionFactory, sql, d.parameters, d.rowMapper)
        return QueryRes(list, d.stats)
    }
}

fun  <T, C, M> jdbcQueryHandler(
            connectionFactory: () -> Connection,
            df: (C, QueryDef<T>) -> JdbcQueryHandlerDelegate<T, C, M>) : (QueryDef<T>, C) -> QueryRes<T, M> {
    return {qd:QueryDef<T>, ctx:C -> JdbcQueryHandlerImpl(connectionFactory, df)(qd, ctx) }
}