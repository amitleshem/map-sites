package com.wixpress.mapsites

import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification

class EventMessageHandler(dao: Dao) {

  def handleMessage(sitePropertyNotification: SitePropertiesNotification) = {

    print("####################")
    println(sitePropertyNotification.event)
    dao.put(sitePropertyNotification)

  }

}
