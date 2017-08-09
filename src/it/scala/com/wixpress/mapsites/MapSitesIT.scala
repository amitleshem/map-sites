package com.wixpress.mapsites

import com.wix.e2e.ResponseMatchers._
import com.wix.e2e.http.sync._
import com.wixpress.hoopoe.ids.randomGuid
import com.wixpress.siteproperties.Country
import com.wixpress.siteproperties.api.v3.Events.{Deleted, Updated}
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.{GeoCoordinates, PostalAddress}
import com.workday.esclient.EsClient
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit
import com.fasterxml.jackson.databind._
import com.wixpress.hoopoe.json.JsonMapper

class MapSitesIT extends SpecificationWithJUnit with BaseE2E {

  trait Ctx extends Scope {
    val topic = "site-properties.changes"
    val kit = EmbeddedEnvironment.testKit
    val requestsDriver = RequestsDriver()
    val postalAddress = PostalAddress(street = "hanamal 40", city = "Tel Aviv", Country.IS , None)


    val esUrl = "http://localhost:9200"
    val client = EsClient.createEsClient(esUrl)
    val indexName = "sites" //index name
    val typeName = "site" //type for documents

//    val mapper = JsonMapper.objectMapperFromTemplate
//      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//    def deserialize(payload: Array[Byte]): Option[Address] = {
//      mapper.readValue(payload, classOf[Address])
//    }

  }

  sequential

  "map sites" should {

    "produce event and save address to DB" in new Ctx {
      val guid = randomGuid

      val version = kit.givenProperty(guid, postalAddress)
      val event = Updated[PostalAddress](version, postalAddress)
      val notification: SitePropertiesNotification = kit.sendNotification(topic, guid, event, Seq.empty)

      requestsDriver.getAddress(guid) must beSuccessfulWith(requestsDriver.anAddress("hanamal 40", "Tel Aviv", Country.IS.numeric(),
      Some(GeoCoordinates(31.98960170, 34.77902670)))).eventually
    }
//
//    "delete a business from DB" in new Ctx {
//          val guid = randomGuid
//
//          val versionUpdate = kit.givenProperty(guid, postalAddress)
//          val updateEvent = Updated[PostalAddress](versionUpdate, postalAddress)
//          val notification1: SitePropertiesNotification = kit.sendNotification(topic, guid, updateEvent, Seq.empty)
//
//          val versionDelete = kit.deletePostalAddress(guid)
//          val deleteEvent = Deleted[PostalAddress](versionDelete, classOf[PostalAddress])
//          val notification2: SitePropertiesNotification = kit.sendNotification(topic, guid, deleteEvent, Seq.empty)
//
//          requestsDriver.getAddress(guid) must beSuccessful and haveBody(None)
//
//        }


  }

}

