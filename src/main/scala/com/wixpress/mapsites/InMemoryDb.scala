package com.wixpress.mapsites

import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.api.v3.MetaSite
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON
import net.liftweb.json._
import net.liftweb.json.Serialization.write

class InMemoryDb extends Dao {

  val businessToAddress: mutable.Map[Guid[_], Address] = mutable.Map()
  val notifications: mutable.ListBuffer[SitePropertiesNotification] = new ListBuffer[SitePropertiesNotification]

  override def allSites(): mutable.Map[Guid[_], Address] = {
    businessToAddress
  }

  override def put(metaSiteId: MetaSiteId, address: Address): Unit = {
    businessToAddress(metaSiteId) = address
//    implicit val formats = DefaultFormats
//    val jsonString = write(address)
  }

  override def get(metaSiteId: Guid[_]): Option[Address] = {
    businessToAddress.get(metaSiteId)
  }

  override def delete(metasiteId: Guid[_]): Unit = {
    //businessToAddress -= metasiteId
    businessToAddress.remove(metasiteId)
  }

}
