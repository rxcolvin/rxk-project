package com.rxk.oauth_helper

import java.sql.DriverManager

/**
 * Some stuff to help setup and test Auth locally
 */

fun main(args: Array<String>) {



    val outletDaoConfig = JdbcConfig("oracle.jdbc.OracleDriver",
                                     "jdbc:oracle:thin:@//tstdb:1801/TSTDB",
                                     "distribution_owner",
                                     "Xd_9XHEMhHyW0-eZ6hss9jtnYPyFIR8TfAlS1wZBjmGn0AzfoFWowQocHo5HAzrfVSJodeqPG2-lZFolLGK8i8Hmvvy2utnUMg-s2NOEuXDnR0elyNUkaXJNwc5O01zPWctRaP8vguG06038se4a2eHJWcM0YZpdNQXw6kzDV_t4Tr0cs7dKAvSrw4PhmRlzlv9NDr3oQ8GAgLdRdzmpyAffFJI34c5TTRMI5KpEKYofG27GSnv7e14qHZUeCBsA-oebsNt-Y56PIm8qaTPQ6rffmsCre4thIpbwQPEN6BuSUDd-yW1tfMZ1xY5ulIrYURkUoVvtA64A0lQLp5k4bw")

    val jdbcConnectionManager = JdbcConnectionManager()

    val connectionFactory = jdbcConnectionManager.connectionFactory(outletDaoConfig)

    val outletDoaModule = outletDaoSqlModule(connectionFactory)


    val res  = outletDoaModule.dao.unique("User", "ao74b0wnz3g31y1keelx8gd53")

    println(res.dataum.name)
}