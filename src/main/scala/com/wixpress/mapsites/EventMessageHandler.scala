package com.wixpress.mapsites

import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.PostalAddress
import com.wixpress.siteproperties.api.v3.SitePropertiesStorageV3

class EventMessageHandler(dao: Dao, siteProperties: SitePropertiesStorageV3) {

  def handleMessage(sitePropertyNotification: SitePropertiesNotification) = {

    val siteSnapshot = siteProperties.readSnapshot(sitePropertyNotification.metasiteId,null)


    println("$$$$$$$$$$$$$$")
    val country =
    {
      for {
        postalAddress <- siteSnapshot[PostalAddress]
        country <- Option(postalAddress.country)
      } yield country
    } getOrElse{
      println("%%%%%%%%%%%%%")
    }




    dao.put(sitePropertyNotification)
  }

}
