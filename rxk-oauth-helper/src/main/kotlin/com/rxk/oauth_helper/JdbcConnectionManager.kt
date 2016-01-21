package com.rxk.oauth_helper

import com.performfeeds.security.crypto.PublicKeyCryptoProvider
import com.performfeeds.security.crypto.impl.RSACryptoProvider
import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by richard.colvin on 19/01/2016.
 */

class JdbcConnectionManager {
    val decrypter: PublicKeyCryptoProvider = RSACryptoProvider()
    val keyProvider: String = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCp1lT7B+nGgVOya/rccHiP5UQ1Rq8wgjpjfEADw1H2ouTwrBo6PfxD7bA0515QDYaJLUSbyssmuy6s0vMl3bboWMqb9hf6ON/i8aGaXIlayHgzs8kLNov/hNIPYY0YSu9FhY6NvVE1QeWKsnRHeZNqpV2LXmx26B2P/qIET9pau/ub9utywkJRkYQTlJQ+LTL2Ebq3H2F3l0fPVXAwNQj+q66q/RdC93aRV0BITJewNG2zHAVApWGYFlbF6jBBZpPByCAgjQPRL0zsnR4cIE6baRcmlcWv/39bIG2/4LNtIpt7DdyB16rI1I7XxqBQwAk+jyuE0IWdcnkOGXj69nmNAgMBAAECggEACm5Ab95vjCJ7OnUJRiqeLPA+vSrnLYqB/YQvBkwjp4sflmxre/I8oQtjDAy9rRr3jUs7cHoG+gz68+BM0KGKT0DMyMFXfaWqkmyWqT1PrkSrpTlAerDR7lKA+DLckIZhpZprQ+dBqoyuhVMqcw1TcXRQh3O4I6Fj12kjGDKzTWkuhujblnDhfV0/k8w2ncj1GB2Vc6lDrrALEAgZLxxcyFSrAVT05N+aDXHMboMEJblQrubkSkqsRSaQppxtNgiNB7hQdxNCHkk+JFnTjqfKgZL+aS7+g1Nuqymn+GhrL0MY7/AkoWyRfmr2ZzjucGRZaRtE+qKN88m5qojZLw3z3QKBgQDcZPKmblr2eqOcTSoUPB347fE2xosW6ezAMfq8GHnLReV4W/yABNqqjOsP1hqd52G/4hubvOInlW7DKoHPn5O0GpJ5nmbHu0+osLwUBJEkXJtiEycExr7Qla0eWS1emwyxLFaV4Q79GzKHgpVv1HeFFXc96jZETrcdf9LIb9/PywKBgQDFRnKWmaYTiSj+7GZnNAO46f/6eq2lklOZ3SMqMHAiI0efMGl0+MEWPQnN160hvkyUPAINuYJQ+2oV6tehKM2neZWtq7MJzgseUBtOHbTe+vi0HE+r1p7g2ioIGUWECLPDkkGzzCXsxdYhZeX4HPTkPCGG1BC1jRUy9fqjkxgBBwKBgFIcpi8N1IQaYxSbxz6suzoAZKtUw4Nw+g0NUe58a/wo+XqjQurrZBDA2d8XlOkZyNh1xHV8pQG8cfzyvFR/jsc+Uy5OrtphVidyWVBX9z+F5TpgClyEM5mA+nPhI78oo0zrSQMkS7JPTL8iDs0QastBmSCPP1KpxR2PfxfAXrGLAoGASZIZP/LzTcvUPyX4lrp+POL/tPE1e08T4IjpCV3hI8oWdta+LYJruBhxZJPvnGr08j9i4K1zXTI2ARCA9DhcYf3lBUzIS3rNCLQFSt+nSZU9VSZNzB8RR3f1Pun10+TO7bvIxv32ktPBJNl0p1BdQBTwx/CkfqCr7EJyA7jitdcCgYEAw/P9d/clsLZJlE0Qk1Xnkabi7Oqr+TrC9DIHRb+VJ7zkUMupSwdmGDM00dMNPTeMxX92OrlwHfme1XNiXXXJBawMuxTecy+pi9H8VcsvudP+MbyLAJLYHC/VNEmXESsYntFm4vYkAzZKe3K6z7Kx3OSA5BhzQbJkkn6z+SQ+ABQ="

    fun connectionFactory(c: JdbcConfig) : () -> Connection {
        Class.forName(c.driverClass)
        val pwd = decrypt(c.dbPasswd)
        return {DriverManager.getConnection(c.connectionString, c.dbUser, pwd)};
    }

    fun decrypt(source: String): String {
        return decrypter.decryptFromBase64String(keyProvider, source)
    }
}


data class JdbcConfig(val driverClass: String, val connectionString:String, val dbUser:String, val dbPasswd:String)


