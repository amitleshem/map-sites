package com.wixpress.mapsites

import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class InMemoryDb extends Dao {

  val businessToAddress: mutable.Map[Guid[_], Address] = mutable.Map()
  val notifications: mutable.ListBuffer[SitePropertiesNotification] = new ListBuffer[SitePropertiesNotification]

  override def put(metaSiteId: MetaSiteId, address: Address): Unit = {
    businessToAddress(metaSiteId) = address
  }

  override def get(metaSiteId: Guid[_]): Option[Address] = {
    businessToAddress.get(metaSiteId)
  }
}
