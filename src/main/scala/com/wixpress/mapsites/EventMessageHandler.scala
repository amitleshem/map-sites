package com.wixpress.mapsites

import com.wixpress.siteproperties.api.v3.Events.{Deleted, Updated}
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.PostalAddress
import com.wixpress.siteproperties.api.v3.{Events, SitePropertiesStorageV3}
import com.wixpress.siteproperties.api.v3.Property

class EventMessageHandler(dao: Dao, siteProperties: SitePropertiesStorageV3) {


  def handleMessage(sitePropertyNotification: SitePropertiesNotification) = {

    sitePropertyNotification.event match {
      case event: Updated[Property] => update(sitePropertyNotification)
      case event: Deleted[Property] => delete(sitePropertyNotification)
    }
  }

  private def update(sitePropertyNotification: SitePropertiesNotification) = {
    val siteSnapshot = siteProperties.readSnapshot(sitePropertyNotification.metasiteId, None)
    val address = siteSnapshot[PostalAddress]
      .map(address => new Address(address.street, address.city, address.country, address.coordinates))
      .getOrElse("Not valid address")

    address match {
      case add: Address => dao.put(sitePropertyNotification.metasiteId, add)
    }
  }

  private def delete(sitePropertyNotification: SitePropertiesNotification) = {
    dao.delete(sitePropertyNotification.metasiteId)
  }
}

