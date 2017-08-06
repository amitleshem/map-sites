package com.wixpress.mapsites

import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid


trait Dao {
  def get(metaSiteId: Guid[_]): Option[Address]

  def put(metaSiteId: MetaSiteId, address: Address)

}
