package com.rxk.oauth_helper

import com.performfeeds.outlet.model.Outlet
import org.springframework.context.support.ClassPathXmlApplicationContext
import javax.xml.bind.JAXBContext

/**
 * Some stuff to help setup and test Auth locally
 */

fun main(args: Array<String>) {

    val outletDaoConfig = DaoConfig("", "")

    val outletDoaModule = OutletDaoModule(outletDaoConfig)
}