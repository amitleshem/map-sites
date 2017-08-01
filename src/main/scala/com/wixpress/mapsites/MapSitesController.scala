package com.wixpress.mapsites

import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import org.springframework.ui.Model
import org.springframework.web.bind.annotation._


@RestController
class MapSitesController(dao: Dao) {

  @RequestMapping(value = Array("/test"), method = Array(RequestMethod.GET))
  def testServer(model: Model): MapSites = {
    val maybeNotification = dao.get()
    val notifications = maybeNotification.map(Seq(_)).getOrElse(Seq.empty)
    MapSites(notifications)
  }
  @RequestMapping(value = Array("/checkdb"), method = Array(RequestMethod.GET))
  def checkDB(model: Model): MapSites = {
    MapSites(Seq.empty)
  }
}

case class MapSites(seqData: Seq[SitePropertiesNotification])