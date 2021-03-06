package com.wixpress.mapsites

import com.wix.e2e.http.Implicits.defaultServerPort
import com.wix.e2e.http.sync._
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.api.v2.GeoCoordinates
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationWithJUnit

case class RequestsDriver() extends SpecificationWithJUnit{

  def getSites = get(s"/getSites")

  def getAddress(guid: Guid[_]) = get(s"/getAddress/${guid}")

  def deleteSite(guid: Guid[_]) = {
    delete(s"/removeSite/${guid}")
  }

  def anAddress(withStreet: String, withCity: String, withCountry: Int, withCoordinates: Option[GeoCoordinates]): Matcher[Address] = {
    be_===(withStreet) ^^ {(_: Address).street} and
    be_===(withCity) ^^ {(_: Address).city} and
    be_===(withCountry) ^^ {(_: Address).country} and
    be_===(withCoordinates) ^^ {(_: Address).coordinates}
  }

}
