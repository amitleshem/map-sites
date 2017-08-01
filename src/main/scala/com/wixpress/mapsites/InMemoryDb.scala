package com.wixpress.mapsites

import com.wixpress.siteproperties.api.v3.Notifications
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class InMemoryDb extends Dao {

  val businessToAddress: mutable.Map[String, String] = mutable.Map()
  val notifications: mutable.ListBuffer[SitePropertiesNotification] = new ListBuffer[SitePropertiesNotification]
  override def put(sitePropertyNotification: Notifications.SitePropertiesNotification): Unit = {
    notifications += sitePropertyNotification
  }

  override def get(): Option[SitePropertiesNotification] = {
    notifications.headOption
  }
}
