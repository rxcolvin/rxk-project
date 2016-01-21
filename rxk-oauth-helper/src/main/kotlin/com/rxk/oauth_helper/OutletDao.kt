package com.rxk.oauth_helper

import com.performfeeds.outlet.model.Outlet
import java.sql.Connection
import java.sql.ResultSet
import java.util.*


// Public
fun outletDaoSqlModule(cf: () -> Connection):
        SqlDaoModule<String, Outlet, String, String> {

    val byIdHandler = jdbcQueryHandler(cf, ::byIdQueryHandler)

    val handlerResolver: (QueryDef<Outlet>) -> (QueryDef<Outlet>, String) -> QueryRes<Outlet, String> = { qd: QueryDef<Outlet> ->
        when (qd) {
            is ByIdQueryDef<*, *, *> -> byIdHandler
            else -> throw RuntimeException()
        }
    }

    return SqlDaoModule(handlerResolver)
}



//private
private val Y_VALUE = "Y"
private val SELECT =
        "o.id, " +
                "o.name, " +
                "o.uuid, " +
                "o.user_auth_id, " +
                "o.default_locale, " +
                "img.image_domain, " +
                "wp.fld_mask_allowed, " +
                "wp.free_text_search_allowed, " +
                "wp.http_enabled, " +
                "wp.https_enabled, " +
                "wp.b2c_enabled," +
                " wp.accptbl_use_lmt, " +
                "wp.accptbl_use_prd, " +
                "wp.accptbl_use_den_prd, " +
                "o.cdn_cchng_rls_enabled," +
                " o.url_pattern," +
                " org.UUID as ORGN_UUID, " +
                "o.host_whitelist_enabled" +
                " FROM outlet o " +
                " LEFT JOIN IMAGES_DOMAIN img ON (img.id = o.images_domain_id ) " +
                " LEFT JOIN ORGANISATIONS org ON (o.ORGN_ID=org.ORGN_ID), web_pull wp ";




private fun getAuthKeys(connection: Connection, outletId: Long?): List<String> {

    val query = "SELECT auth_key FROM pf_auth_key WHERE outlet_id = ? AND enabled = ?"
    val pstmt = connection.prepareStatement(query)
    var resultSet: ResultSet? = null
    val authKeys = ArrayList<String>()


    pstmt.setLong(1, outletId!!)
    pstmt.setString(2, Y_VALUE)
    resultSet = pstmt.executeQuery()
    while (resultSet!!.next()) {
        authKeys.add(resultSet.getString("auth_key"))
    }

    return authKeys
}


/**
 * Complicated OutletROwMapper
 */
private fun outLetRowMapper(resultSet: ResultSet, connection: Connection): Outlet {
    val outlet = Outlet()
    outlet.id = resultSet.getString("uuid")
    outlet.orgnUuid = resultSet.getString("ORGN_UUID")
    outlet.name = resultSet.getString("name")
    outlet.defaultLocale = resultSet.getString("default_locale")
    outlet.imagesDomain = resultSet.getString("image_domain")
    outlet.isHttpEnabled = resultSet.getYorN("http_enabled")
    outlet.isHttpsEnabled = resultSet.getYorN("https_enabled")
    outlet.isFldMaskAllowed = resultSet.getYorN("fld_mask_allowed")
    outlet.isFreeTextSearchAllowed = resultSet.getYorN("free_text_search_allowed")
    outlet.isB2cBanned = !resultSet.getYorN("b2c_enabled")
    outlet.acceptableUseLimit = resultSet.getLong("accptbl_use_lmt")
    outlet.acceptableUsePeriod = resultSet.getLong("accptbl_use_prd")
    outlet.acceptableUseDenyPeriod = resultSet.getLong("accptbl_use_den_prd")
    outlet.isUseCDNCachingRules = resultSet.getYorN("cdn_cchng_rls_enabled")

    val outletId = resultSet.getLong("id")
    val userAuthId = resultSet.getLong("user_auth_id")

    // Authentication keys
    outlet.authKeys = getAuthKeys(connection, outletId)
    // IP whitelist


    //    outlet.ipWhitelist = getIpWhitelist(connection, outletId)
    //    // Valid domains
    //    outlet.validDomainsByAsset = getValidDomainsByAsset(connection, outletId)
    //    // Valid domains for old schema
    //    outlet.validDomains = getValidDomainsByAssetOld(connection, outletId)
    //    // Referrer whitelist
    //    outlet.referrerWhitelist = getRefererWhitelist(connection, outletId)
    //    // Host page whitelist
    //    if (resultSet.getYorN("host_whitelist_enabled")) {
    //        outlet.hostPageWhitelist = this.getHostPageWhitelist(connection, outletId)
    //    }
    //    // Cache settings
    //    outlet.cacheSettings = getCacheSettings(connection, outletId)
    //    // Default fields
    //    outlet.defaultFields = getDefaultFields(connection, outletId)
    //    // vod fields
    //    getVodFields(connection, outletId, outlet)
    //    // formatUuids
    //    outlet.formatUuids = getFormatUuids(connection, outletId)
    //    // organisationUuidKeys
    //    outlet.organisationUuidKeys = getOrganisationUuidKeys(connection, outletId)
    //    // tracking assets
    //    outlet.trackingAssets = getTrackingAssets(connection, outletId)
    //    // tracking sites
    //    outlet.trackingSites = getTrackingSites(connection, outletId)
    //    // tracking settings
    //    updateTrackingSetting(connection, outletId, outlet)
    //    if (userAuthId != null) {
    //        getUserAuth(connection, userAuthId, outlet)
    //    }
    //    // sanitizer configuration
    //    outlet.sanitizerConfig = getSanitizerConfig(connection, outletId)
    //    // hosted live player configuration
    //    outlet.livePlayer = getLivePlayer(connection, outletId)
    //    // user auth custom properties
    //    if (userAuthId != null) {
    //        outlet.userAuthCustomProperties = getUserAuthCustomProperties(connection, userAuthId)
    //    }
    //    outlet.urlPattern = resultSet.getString("url_pattern")
    //    // set ePlayer v.3 Publisher data part
    //    getEPlayerPublisher(connection, outletId, outlet)
    //    // set ePlayer v.3 Advertisements settings getAdvertisementsSettings(connection, outletId, outlet)


    return outlet
}



private fun byIdQueryHandler(ctx: String, qd: QueryDef<Outlet>) = OutletByIdQueryHandlerDelegate(ctx, qd as ByIdQueryDef<String, Outlet, String >)

private class OutletByIdQueryHandlerDelegate(val ctx: String, val qd: ByIdQueryDef<String, Outlet, String>) : JdbcQueryHandlerDelegate<Outlet, String, String> {
    override val stats: String = "Hello"
    override val parameters: Array<Any> = arrayOf(qd.id)
    override val select: String = SELECT
    override val where: String = "o.id = wp.outlet_id AND o.uuid = ?"
    override val rowMapper = ::outLetRowMapper
}













//private fun getUserAuth(connection: Connection, id: Long?, outlet: Outlet) {
//
//    val query = "SELECT id, USER_AUTH_UUID, enabled, token_encryption_method, public_key, private_key," + " auth_type, password_hash_algorithm, token_hash_algorithm, default_cookie_domain FROM user_auth WHERE id = ?"
//    var pstmt: PreparedStatement? = null
//    var resultSet: ResultSet? = null
//    var userAuthId: Long? = null
//
//    try {
//        pstmt = connection.prepareStatement(query)
//        pstmt!!.setLong(1, id!!)
//        resultSet = pstmt.executeQuery()
//        if (resultSet!!.next()) {
//            userAuthId = resultSet.getLong("id")
//            outlet.userAuthId = resultSet.getString("USER_AUTH_UUID")
//            outlet.isOauthEnabled = Y_VALUE == resultSet.getString("enabled")
//            outlet.encryptionMethod = resultSet.getString("token_encryption_method")
//
//            outlet.encryptionPublicKey = resultSet.getString("public_key")
//            outlet.encryptionPrivateKey = resultSet.getString("private_key")
//            outlet.oauthType = resultSet.getString("auth_type")
//            outlet.passwordHashAlgorithm = resultSet.getString("password_hash_algorithm")
//            outlet.tokenHashAlgorithm = resultSet.getString("token_hash_algorithm")
//            outlet.defaultCookieDomain = resultSet.getString("default_cookie_domain")
//        }
//    } finally {
//        this.close(resultSet, pstmt)
//    }
//
//    if (userAuthId != null) {
//        // User_Auth Salt
//        outlet.salts = this.getSalts(connection, userAuthId)
//
//        // Login_Cookie_Defn
//        outlet.loginCookieDefns = this.getLoginCookieDefns(connection, userAuthId)
//
//        // User_Auth_Scope
//        outlet.userAuthScopes = this.getScopes(connection, userAuthId)
//    }
//
//}