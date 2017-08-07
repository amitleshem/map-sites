package com.wixpress.mapsites

import com.google.maps.model.GeocodingResult
import com.google.maps.{GeoApiContext, GeocodingApi}
import com.wix.e2e.http.Implicits.defaultServerPort
import com.wix.e2e.http.sync._
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.Country
import com.wixpress.siteproperties.api.v2.GeoCoordinates
import com.wixpress.siteproperties.api.v3.Properties.PostalAddress
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationWithJUnit

case class RequestsDriver() extends SpecificationWithJUnit{

  def getSites = get(s"/getSites")

  def getAddress(guid: Guid[_]) = get(s"/getAddress/${guid}")

  def deleteSite(guid: Guid[_]) = {
    delete(s"/removeSite/${guid}")
  }

  def anAddress(withStreet: String, withCity: String, withCountry: Country, withCoordinates: Option[GeoCoordinates]): Matcher[Address] = {
    be_===(withStreet) ^^ {(_: Address).street} and
    be_===(withCity) ^^ {(_: Address).city} and
    be_===(withCountry) ^^ {(_: Address).country} and
    be_===(withCoordinates) ^^ {(_: Address).coordinates}
  }

}
