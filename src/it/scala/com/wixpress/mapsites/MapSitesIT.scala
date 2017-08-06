package com.wixpress.mapsites

import com.wix.e2e.ResponseMatchers.beSuccessfulWith
import com.wixpress.hoopoe.ids.randomGuid
import com.wixpress.siteproperties.Country
import com.wixpress.siteproperties.api.v3.Commands
import com.wixpress.siteproperties.api.v3.Events.Updated
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.PostalAddress
import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit

class MapSitesIT extends SpecificationWithJUnit with BaseE2E {

  trait Ctx extends Scope {
    val topic = "site-properties.changes"
    val kit = EmbeddedEnvironment.testKit
    val requestsDriver = RequestsDriver()
  }

  sequential

  "map sites" should {

    "produce event and save address to DB" in new Ctx {
      val postalAddress = PostalAddress(street = "hanamal 40", city = "Tel Aviv", Country.IS)
      val guid = randomGuid
      val cmd = Commands.Update(postalAddress)
      val version = kit.givenProperty(guid, postalAddress)
      val event = Updated[PostalAddress](version, postalAddress)
      val notification: SitePropertiesNotification = kit.sendNotification(topic, guid, event, Seq.empty)
      requestsDriver.getAddress(guid) must beSuccessfulWith(requestsDriver.anAddress("hanamal 40", "Tel Aviv", Country.IS)).eventually
    }

  }

}

