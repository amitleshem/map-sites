package com.wixpress.mapsites

import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.api.v3.MetaSite

import scala.collection.mutable


trait Dao {

  def allSites(): mutable.Map[Guid[_], Address]

  def delete(metasiteId: Guid[_])

  def get(metaSiteId: Guid[_]): Option[Address]

  def put(metaSiteId: MetaSiteId, address: Address)

}
