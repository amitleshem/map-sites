package com.wixpress.mapsites

import com.wixpress.siteproperties.api.v3.Notifications
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification


trait Dao {
  def get(): Option[SitePropertiesNotification]

  def put(sitePropertyNotification: Notifications.SitePropertiesNotification)


}
