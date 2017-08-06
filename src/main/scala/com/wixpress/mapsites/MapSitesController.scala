package com.wixpress.mapsites

import com.wixpress.hoopoe.ids.Guid
import org.springframework.web.bind.annotation._


@RestController
class MapSitesController(dao: Dao) {

  @RequestMapping(value = Array("/getAddress/{id}"), method = Array(RequestMethod.GET))
  def getAddress(@PathVariable id: Guid[_]): Option[Address] = {
    dao.get(id)
  }

//  @RequestMapping(value = Array("/removeSite/{id}"), method = Array(RequestMethod.DELETE))
//  def removeSite(@PathVariable id: Guid[_]): Unit = {
//    dao.delete(id)
//  }

}

