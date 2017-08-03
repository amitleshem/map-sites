package com.wixpress.mapsites

import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.api.v3.Notifications
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification


trait Dao {
  def get(metaSiteId: Guid[_]): Option[Address]

  def put(metaSiteId: MetaSiteId, address: Address)

}
