package com.wixpress.mapsites

import java.io.StringReader

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.wix.e2e.ResponseMatchers._
import com.wixpress.hoopoe.ids.randomGuid
import com.wixpress.petri.laboratory.UserInfo
import com.wixpress.siteproperties.Country
import com.wixpress.siteproperties.api.v3.Events.{Deleted, Updated}
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.{GeoCoordinates, PostalAddress}
import com.workday.esclient.EsClient
import net.liftweb.json.DefaultFormats
import org.specs2.matcher.Scope
import org.specs2.mutable.SpecificationWithJUnit
import net.liftweb.json._
import net.liftweb.json.Serialization.write

class MapSitesIT extends SpecificationWithJUnit with BaseE2E {

  trait Ctx extends Scope {
    val topic = "site-properties.changes"
    val kit = EmbeddedEnvironment.testKit
    val requestsDriver = RequestsDriver()
    val postalAddress = PostalAddress(street = "hanamal 40", city = "Tel Aviv", Country.IS, None)
  }

  trait ElasticCtx extends Scope {

    val esUrl = "http://localhost:9200"
    val client = EsClient.createEsClient(esUrl)
    val indexName = "pres" //index name
    val typeName = "president" //type for documents

  }

  sequential

  "map sites" should {

    "produce event and save address to DB" in new Ctx {
      val guid = randomGuid

      val version = kit.givenProperty(guid, postalAddress)
      val event = Updated[PostalAddress](version, postalAddress)
      val notification: SitePropertiesNotification = kit.sendNotification(topic, guid, event, Seq.empty)

      requestsDriver.getAddress(guid) must beSuccessfulWith(requestsDriver.anAddress("hanamal 40", "Tel Aviv", Country.IS,
      Some(GeoCoordinates(31.98960170, 34.77902670)))).eventually
    }

    "delete a business from DB" in new Ctx {
          val guid = randomGuid

          val versionUpdate = kit.givenProperty(guid, postalAddress)
          val updateEvent = Updated[PostalAddress](versionUpdate, postalAddress)
          val notification1: SitePropertiesNotification = kit.sendNotification(topic, guid, updateEvent, Seq.empty)

          val versionDelete = kit.deletePostalAddress(guid)
          val deleteEvent = Deleted[PostalAddress](versionDelete, classOf[PostalAddress])
          val notification2: SitePropertiesNotification = kit.sendNotification(topic, guid, deleteEvent, Seq.empty)

          requestsDriver.getAddress(guid) must beSuccessful and haveBody(None)

        }


    "embedded elastic index and get" in new ElasticCtx {

      client.deleteDocsByQuery(indexName) //defaults to "match_all" query
      client.deleteIndex(indexName)

      val address = Address("hanamal 40", "tel aviv", Country.IS, None)
      implicit val formats = DefaultFormats
      val jsonString = write(address)

      val id = "1" //document ID

      client.createIndex(indexName) //creates index in ES
      client.index(indexName, typeName, id, jsonString) //indexes doc to that index
      val getDoc = client.get(indexName, id) //get the doc within an EsResponse object

      }

  }

}

