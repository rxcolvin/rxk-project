package com.rxk.oauth_helper

import com.performfeeds.outlet.model.Outlet

/**
 * Created by richard.colvin on 19/01/2016.
 */
class JdbcDaoModule<T>(config: DaoConfig, jdbcConnectionManager: (DaoC: Array<JdbcQueryHandler<Outlet, String, String>>) {
     val dao: QueryDao<String, T, String, String> = JDBCQueryDaoImpl()


}

data class DaoConfig(val dbUser:String, val dbPasswd:String)


class OutletDaoJdbcModule(override val config: DaoConfig) : JdbcDaoModule<Outlet>(){

}

fun outletDaoJDBCModule(config: DaoConfig) : JdbcDaoModule<Outlet> {
    return Jdbc
}