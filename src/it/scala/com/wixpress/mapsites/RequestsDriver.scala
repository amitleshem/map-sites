package com.wixpress.mapsites

import com.wix.e2e.http.Implicits.defaultServerPort
import com.wix.e2e.http.sync._
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.siteproperties.Country
import org.specs2.matcher.Matcher
import org.specs2.mutable.SpecificationWithJUnit

case class RequestsDriver() extends SpecificationWithJUnit{

  def getAddress(guid: Guid[_]) = get(s"/getAddress/${guid}")

  def anAddress(withStreet: String, withCity: String, withCountry: Country): Matcher[Address] = {
    be_===(withStreet) ^^ {(_: Address).street} and
    be_===(withCity) ^^ {(_: Address).city} and
    be_===(withCountry) ^^ {(_: Address).country}
  }

}
