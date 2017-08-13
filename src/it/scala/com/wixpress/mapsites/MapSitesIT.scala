package com.wixpress.mapsites

import com.wix.e2e.ResponseMatchers._
import com.wixpress.framework.rpc.client.RpcProxyFactoryTestBuilder
import com.wixpress.hoopoe.ids.{Guid, randomGuid}
import com.wixpress.siteproperties.Country
import com.wixpress.siteproperties.api.v3.Events.{Deleted, Updated}
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.Properties.{GeoCoordinates, PostalAddress}
import org.specs2.matcher.{Matcher, Scope}
import org.specs2.mutable.SpecificationWithJUnit
import spray.http.HttpResponse

class MapSitesIT extends SpecificationWithJUnit with BaseE2E {

  trait Ctx extends Scope {
    val topic = "site-properties.changes"
    val kit = EmbeddedEnvironment.testKit
    val requestsDriver = RequestsDriver()
    val postalAddress = PostalAddress(street = "hanamal 40", city = "Tel Aviv", Country.IS , None)

    def sendNotificationToKafka(guid: Guid[_]) = {
      val version = kit.givenProperty(guid, postalAddress)
      val event = Updated[PostalAddress](version, postalAddress)
      kit.sendNotification(topic, guid, event, Seq.empty)
    }

    //def stringToOptAddress(s: String): Option[Address] = objectMapper.readValue(s, classOf[Option[Address]])
    //def notHaveAddress(): Matcher[HttpResponse] = beNone ^^ {(h:HttpResponse) => stringToOptAddress(h.entity.asString)}

    val rpcClient = RpcProxyFactoryTestBuilder
      .aTestRpcClient
      .builderFor[MapSitesApi]
      .withBaseUrl("localhost:9901")
      .build()

  }

  sequential

  "map sites" should {

    "produce event and save address to DB in RPC" in new Ctx {
      val guid = randomGuid
      sendNotificationToKafka(guid)
      rpcClient.getAddress(guid) must beSome(requestsDriver.anAddress("hanamal 40", "Tel Aviv", Country.IS.numeric(),
              Some(GeoCoordinates(31.98960170, 34.77902670)))).eventually
    }


    "delete address from DB in RPC" in new Ctx {
      val guid = randomGuid
      sendNotificationToKafka(guid)
      eventually{
        rpcClient.getAddress(guid) must beSome(requestsDriver.anAddress("hanamal 40", "Tel Aviv", Country.IS.numeric(),
          Some(GeoCoordinates(31.98960170, 34.77902670)))).eventually
      }

      val versionDelete = kit.deletePostalAddress(guid)
      val deleteEvent = Deleted[PostalAddress](versionDelete, classOf[PostalAddress])
      val notification2: SitePropertiesNotification = kit.sendNotification(topic, guid, deleteEvent, Seq.empty)
      rpcClient.getAddress(guid) must beNone.eventually

    }

  }

}

