package com.wixpress.mapsites

import com.wix.e2e.ResponseMatchers.beSuccessfulWith
import com.wix.e2e.http.Implicits.defaultServerPort
import com.wix.e2e.http.sync.get
import com.wixpress.greyhound.GreyhoundProducerBuilder
import com.wixpress.hoopoe.ids.randomGuid
import com.wixpress.siteproperties.Country
import com.wixpress.siteproperties.api.v3.Events.Updated
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.PostalAddress
import com.wixpress.siteproperties.api.v3.Version
import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit

class MapSitesIT extends SpecificationWithJUnit with BaseE2E {

  trait Ctx extends Scope {
    val topic = "site-properties.changes"
  }

  "map sites" should {

    "return empty list" in new Ctx {
      get("/test") must beSuccessfulWith(MapSites(Seq.empty))
    }

    "send and receive with kafka and controller" in new Ctx {
      val producer1 = GreyhoundProducerBuilder.aGreyhoundProducerBuilder(topic).build
      val notification1 = SitePropertiesNotification(metasiteId = randomGuid, Updated[PostalAddress](Version.maximumVersion, null), Seq.empty)
      producer1.produce(notification1)
      get("/test") must beSuccessfulWith(MapSites(Seq(notification1))).eventually
    }

    "produce event and save address to DB" in new Ctx {
      val producer2 = GreyhoundProducerBuilder.aGreyhoundProducerBuilder(topic).build
      val notification2 = SitePropertiesNotification(metasiteId = randomGuid, Updated[PostalAddress](Version.maximumVersion,
        PostalAddress(street = "hanamal 40", city = "Tel Aviv", Country.IS, null, null, null, null, true, null, null)), Seq.empty)
      producer2.produce(notification2)
      get("/checkdb") must beSuccessfulWith(MapSites(Seq(notification2))).eventually
    }


  }

}

