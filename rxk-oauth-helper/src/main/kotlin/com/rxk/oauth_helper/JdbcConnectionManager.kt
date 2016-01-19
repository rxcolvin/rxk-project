package com.rxk.oauth_helper

import java.sql.Connection
import java.sql.DriverManager

/**
 * Created by richard.colvin on 19/01/2016.
 */

class JdbcConnectionManager {

    fun connectionFactory(jdbcConfig: JdbcConfig) : () -> Connection {
        Class.forName("oracle.jdbc.OracleDriver")
        val encrytedPwd = "Xd_9XHEMhHyW0-eZ6hss9jtnYPyFIR8TfAlS1wZBjmGn0AzfoFWowQocHo5HAzrfVSJodeqPG2-lZFolLGK8i8Hmvvy2utnUMg-s2NOEuXDnR0elyNUkaXJNwc5O01zPWctRaP8vguG06038se4a2eHJWcM0YZpdNQXw6kzDV_t4Tr0cs7dKAvSrw4PhmRlzlv9NDr3oQ8GAgLdRdzmpyAffFJI34c5TTRMI5KpEKYofG27GSnv7e14qHZUeCBsA-oebsNt-Y56PIm8qaTPQ6rffmsCre4thIpbwQPEN6BuSUDd-yW1tfMZ1xY5ulIrYURkUoVvtA64A0lQLp5k4bw"
        val pwd = decrypt(encrytedPwd)
        return {DriverManager.getConnection("jdbc:oracle:thin:@//tstdb:1801/TSTDB", "distribution_owner", pwd)};

    }

}

data class JdbcConfig(val driverClass: String, val connectionString:String, val dbUser:String, val dbPasswd:String)
