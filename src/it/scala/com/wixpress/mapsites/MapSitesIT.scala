package com.wixpress.mapsites

import com.wix.e2e.ResponseMatchers.beSuccessfulWith
import com.wix.e2e.http.Implicits.defaultServerPort
import com.wix.e2e.http.sync.get
import com.wixpress.hoopoe.ids.randomGuid
import com.wixpress.siteproperties.Country
import com.wixpress.siteproperties.api.v3.Commands
import com.wixpress.siteproperties.api.v3.Events.Updated
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.PostalAddress
import org.specs2.matcher.{Matcher, Scope}
import org.specs2.mutable.SpecificationWithJUnit

class MapSitesIT extends SpecificationWithJUnit with BaseE2E {

  trait Ctx extends Scope {
    val topic = "site-properties.changes"

    def anAddress(withStreet: String): Matcher[Address] = {
      be_===(withStreet) ^^ {(_: Address).street}
    }
  }

  sequential

  "map sites" should {

//    "return empty list" in new Ctx {
//      get("/test") must beSuccessfulWith(MapSites(Seq.empty))
//    }



    "produce event and save address to DB" in new Ctx {

      // TODO: Move to driver [givenEventSent()]
      val postalAddress = PostalAddress(street = "hanamal 40", city = "Tel Aviv", Country.IS)
      val guid = randomGuid
      val kit = EmbeddedEnvironment.testKit

      val cmd = Commands.Update(postalAddress)
      val version = kit.givenProperty(guid, postalAddress)
      val event = Updated[PostalAddress](version, postalAddress)
      val notification: SitePropertiesNotification = kit.sendNotification(topic, guid, event, Seq.empty)

      val address = new Address("hanamal 40", "Tel Aviv", Country.IS, None)

      get(s"/getAddress/${guid}") must beSuccessfulWith(anAddress("hanamal 40")).eventually
    }

  }

}

