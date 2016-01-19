package com.rxk.oauth_helper

import com.performfeeds.security.crypto.PublicKeyCryptoProvider
import com.performfeeds.security.crypto.impl.RSACryptoProvider
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*
import kotlin.collections.indices

/**
 * Created by richard.colvin on 19/01/2016.
 */

interface QueryDao<K, T, C, M> {
    fun get(ctx: C, id: K): T
    fun query(ctx: C, queryDef: QueryDef<T>): QueryRes<T, M>
}

data class QueryRes<T, M>(val data: Collection<T>, val stats: M)


interface QueryDef<T> {

}


class JDBCQueryDaoImpl<K, T, C, M>(
        val connectionFactory: () ->Connection,
        val byIdHandler: (() -> Connection, K, C) -> T,
        val handlerResolver: (QueryDef<T>) -> (() -> Connection, QueryDef<T>, C) -> QueryRes<T, M>
) : QueryDao<K, T, C, M> {

    override fun get(ctx: C, id: K): T {
        return byIdHandler(connectionFactory, id, ctx)
    }

    override fun query(ctx: C, queryDef: QueryDef<T>): QueryRes<T, M> {
        val res = handlerResolver(queryDef)
        return res(connectionFactory, queryDef, ctx)
    }
}

interface JdbcByIdHandler<T, C, K, M> {
    fun handle(connection: Connection, id: K, ctx: C): QueryRes<T, M>
}

interface JdbcQueryHandler<T, C, M> {
    val queryDefType:Class<QueryDef<T>>
    fun handleQuery(connectionFactory: () -> Connection, queryDef: QueryDef<T>, ctx: C): QueryRes<T, M>
}

fun getConnection(): Connection {
    Class.forName("oracle.jdbc.OracleDriver")
    val encrytedPwd = "Xd_9XHEMhHyW0-eZ6hss9jtnYPyFIR8TfAlS1wZBjmGn0AzfoFWowQocHo5HAzrfVSJodeqPG2-lZFolLGK8i8Hmvvy2utnUMg-s2NOEuXDnR0elyNUkaXJNwc5O01zPWctRaP8vguG06038se4a2eHJWcM0YZpdNQXw6kzDV_t4Tr0cs7dKAvSrw4PhmRlzlv9NDr3oQ8GAgLdRdzmpyAffFJI34c5TTRMI5KpEKYofG27GSnv7e14qHZUeCBsA-oebsNt-Y56PIm8qaTPQ6rffmsCre4thIpbwQPEN6BuSUDd-yW1tfMZ1xY5ulIrYURkUoVvtA64A0lQLp5k4bw"
    val pwd = decrypt(encrytedPwd)
    return DriverManager.getConnection("jdbc:oracle:thin:@//tstdb:1801/TSTDB", "distribution_owner", pwd);
}

fun decrypt(source: String): String {
    val keyProvider: String = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCp1lT7B+nGgVOya/rccHiP5UQ1Rq8wgjpjfEADw1H2ouTwrBo6PfxD7bA0515QDYaJLUSbyssmuy6s0vMl3bboWMqb9hf6ON/i8aGaXIlayHgzs8kLNov/hNIPYY0YSu9FhY6NvVE1QeWKsnRHeZNqpV2LXmx26B2P/qIET9pau/ub9utywkJRkYQTlJQ+LTL2Ebq3H2F3l0fPVXAwNQj+q66q/RdC93aRV0BITJewNG2zHAVApWGYFlbF6jBBZpPByCAgjQPRL0zsnR4cIE6baRcmlcWv/39bIG2/4LNtIpt7DdyB16rI1I7XxqBQwAk+jyuE0IWdcnkOGXj69nmNAgMBAAECggEACm5Ab95vjCJ7OnUJRiqeLPA+vSrnLYqB/YQvBkwjp4sflmxre/I8oQtjDAy9rRr3jUs7cHoG+gz68+BM0KGKT0DMyMFXfaWqkmyWqT1PrkSrpTlAerDR7lKA+DLckIZhpZprQ+dBqoyuhVMqcw1TcXRQh3O4I6Fj12kjGDKzTWkuhujblnDhfV0/k8w2ncj1GB2Vc6lDrrALEAgZLxxcyFSrAVT05N+aDXHMboMEJblQrubkSkqsRSaQppxtNgiNB7hQdxNCHkk+JFnTjqfKgZL+aS7+g1Nuqymn+GhrL0MY7/AkoWyRfmr2ZzjucGRZaRtE+qKN88m5qojZLw3z3QKBgQDcZPKmblr2eqOcTSoUPB347fE2xosW6ezAMfq8GHnLReV4W/yABNqqjOsP1hqd52G/4hubvOInlW7DKoHPn5O0GpJ5nmbHu0+osLwUBJEkXJtiEycExr7Qla0eWS1emwyxLFaV4Q79GzKHgpVv1HeFFXc96jZETrcdf9LIb9/PywKBgQDFRnKWmaYTiSj+7GZnNAO46f/6eq2lklOZ3SMqMHAiI0efMGl0+MEWPQnN160hvkyUPAINuYJQ+2oV6tehKM2neZWtq7MJzgseUBtOHbTe+vi0HE+r1p7g2ioIGUWECLPDkkGzzCXsxdYhZeX4HPTkPCGG1BC1jRUy9fqjkxgBBwKBgFIcpi8N1IQaYxSbxz6suzoAZKtUw4Nw+g0NUe58a/wo+XqjQurrZBDA2d8XlOkZyNh1xHV8pQG8cfzyvFR/jsc+Uy5OrtphVidyWVBX9z+F5TpgClyEM5mA+nPhI78oo0zrSQMkS7JPTL8iDs0QastBmSCPP1KpxR2PfxfAXrGLAoGASZIZP/LzTcvUPyX4lrp+POL/tPE1e08T4IjpCV3hI8oWdta+LYJruBhxZJPvnGr08j9i4K1zXTI2ARCA9DhcYf3lBUzIS3rNCLQFSt+nSZU9VSZNzB8RR3f1Pun10+TO7bvIxv32ktPBJNl0p1BdQBTwx/CkfqCr7EJyA7jitdcCgYEAw/P9d/clsLZJlE0Qk1Xnkabi7Oqr+TrC9DIHRb+VJ7zkUMupSwdmGDM00dMNPTeMxX92OrlwHfme1XNiXXXJBawMuxTecy+pi9H8VcsvudP+MbyLAJLYHC/VNEmXESsYntFm4vYkAzZKe3K6z7Kx3OSA5BhzQbJkkn6z+SQ+ABQ="
    return decrypter.decryptFromBase64String(keyProvider, source)
}

val decrypter: PublicKeyCryptoProvider = RSACryptoProvider()

fun main(args: Array<String>) {
    val x = getConnection().prepareStatement("select * from OUTLET")
    val r = x.executeQuery()
}

abstract class JdbcQueryHandlerBase<T, C, M>(
        val rowMapper: (ResultSet) -> T) : JdbcQueryHandler<T, C, M> {

    override fun handleQuery(connectionFactory: () -> Connection, queryDef: QueryDef<T>, ctx: C): QueryRes<T, M> {

        val list = ArrayList<T>()
        val sql = "SELECT " + select() + " WHERE " + where();

        var connection:Connection? = null
        var st: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            connection = connectionFactory()
            st = connection.prepareStatement(sql)

            val ps = parameters(queryDef, ctx)

            for (i in ps.indices) {
                val x = ps[i]
                when (x) {
                    is String -> st.setString(i + 1, x)
                    is Long -> st.setLong((i + 1), x)
                    is Int -> st.setInt((i+1), x)
                    else -> throw RuntimeException("Spit")
                }
            }

            rs = st.executeQuery();
            while (rs.next()) {
                list.add(rowMapper(rs))
            }

            return QueryRes(list, stats())
        } finally {
            connection?.close()
            rs?.close()
            st?.close()
        }
    }



    fun before() {
    }

    abstract fun stats(): M
    fun after() {
    }

    abstract fun select(): String

    abstract fun where(): String

    abstract fun parameters(queryDef: QueryDef<T>, ctx: C): Array<Any>

    abstract fun create(): T

}

