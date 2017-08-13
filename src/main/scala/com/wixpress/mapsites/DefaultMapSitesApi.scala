package com.wixpress.mapsites

import com.wixpress.hoopoe.ids.Guid


trait MapSitesApi {
  def getAddress(id: Guid[_]): Option[Address]
}

class DefaultMapSitesApi(dao: Dao) extends MapSitesApi {

  def getAddress(id: Guid[_]): Option[Address] = {
    dao.get(id)
  }

}

