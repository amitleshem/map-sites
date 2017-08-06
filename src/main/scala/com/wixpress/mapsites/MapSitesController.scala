package com.wixpress.mapsites

import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import org.springframework.web.bind.annotation._


@RestController
class MapSitesController(dao: Dao) {

  @RequestMapping(value = Array("/getAddress/{id}"), method = Array(RequestMethod.GET))
  def getAddress(@PathVariable id: Guid[_]): Option[Address] = {
    dao.get(id)
  }
}

case class MapSites(seqData: Seq[SitePropertiesNotification])