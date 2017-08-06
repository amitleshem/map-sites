package com.wixpress.mapsites

import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import org.springframework.web.bind.annotation._

import scala.collection.mutable


@RestController
class MapSitesController(dao: Dao) {

  @RequestMapping(value = Array("/getSites"), method = Array(RequestMethod.GET))
  def getSites(): mutable.Map[Guid[_], Address] = {
    dao.allSites()
  }

  @RequestMapping(value = Array("/getAddress/{id}"), method = Array(RequestMethod.GET))
  def getAddress(@PathVariable id: Guid[_]): Option[Address] = {
    dao.get(id)
  }

  @RequestMapping(value = Array("/removeSite/{id}"), method = Array(RequestMethod.GET))
  def removeSite(@PathVariable id: Guid[_]): Unit = {
    dao.delete(id)
  }

}

