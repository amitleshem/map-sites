package com.wixpress.mapsites

import com.google.maps.model.GeocodingResult
import com.google.maps.{GeoApiContext, GeocodingApi}
import com.wixpress.siteproperties.api.v2.GeoCoordinates
import com.wixpress.siteproperties.api.v3.Events.{Deleted, Updated}
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.PostalAddress
import com.wixpress.siteproperties.api.v3.{Property, SitePropertiesStorageV3}

class EventMessageHandler(dao: Dao, siteProperties: SitePropertiesStorageV3, context: GeoApiContext) {

  def handleMessage(sitePropertyNotification: SitePropertiesNotification) = {

    sitePropertyNotification.event match {
      case event: Updated[Property] => update(sitePropertyNotification)
      case event: Deleted[Property] => delete(sitePropertyNotification)
    }
  }


  private def update(sitePropertyNotification: SitePropertiesNotification) = {
    val siteSnapshot = siteProperties.readSnapshot(sitePropertyNotification.metasiteId, None)
    val address = siteSnapshot[PostalAddress]
      .map(address => new Address(address.street, address.city, address.country, getCoordinates(address)))
      .getOrElse("Not valid address")

    address match {
      case add: Address => dao.put(sitePropertyNotification.metasiteId, add)
    }
  }


  private def delete(sitePropertyNotification: SitePropertiesNotification) = {
    dao.delete(sitePropertyNotification.metasiteId)
  }

  private def getCoordinates(address: PostalAddress): Option[GeoCoordinates] ={
    val addressString = address.street + " " + address.city
    val results: Array[GeocodingResult] = GeocodingApi.geocode(context,addressString).await
    val coordinates = results(0).geometry.location
    Some(GeoCoordinates(coordinates.lat, coordinates.lng))
  }

}

