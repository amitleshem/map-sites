package com.wixpress.mapsites

import com.google.maps.GeoApiContext
import com.wixpress.framework.spring.JsonRpcServerConfiguration
import com.wixpress.greyhound.{Consumers, GreyhoundConsumerSpec, GreyhoundSpringConfig, MessageHandler}
import com.wixpress.hoopoe.config.ConfigFactory._
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3._
import org.springframework.context.annotation.{Bean, Configuration, Import}

@Configuration
@Import(Array(classOf[GreyhoundSpringConfig], classOf[JsonRpcServerConfiguration]))
class MapSitesConfig {

  private val config = aConfigFor[ConfigRoot]("map-sites")

  val context: GeoApiContext = new GeoApiContext.Builder().apiKey(config.secret.apiKey).build

  @Bean def dao: Dao = new ElasticSearchDao

  @Bean def mapSitesController(dao: Dao): MapSitesController = new MapSitesController(dao)

  @Bean def eventMessageHandler(dao: Dao): EventMessageHandler = {

    val sitePropertiesStorage = RpcFactory.withSession.builderFor[SitePropertiesStorageV3].withBaseUrl(config.services.sitePropertiesUrl).build()

    new EventMessageHandler(dao, sitePropertiesStorage, context)
  }


  @Bean def consumer(consumers: Consumers, eventMessageHandler: EventMessageHandler) = {
    val messageHandler = MessageHandler.aMessageHandler {
      sitePropertyNotification: SitePropertiesNotification => eventMessageHandler.handleMessage(sitePropertyNotification)
    }.build
    consumers.add(GreyhoundConsumerSpec.aGreyhoundConsumerSpec(topic = "site-properties.changes", messageHandler = messageHandler)
      .withGroup("myGroup"))
    messageHandler
  }


}

case class ConfigRoot(services: Services, secret: Secret)
case class Services(sitePropertiesUrl: String)
case class Secret(apiKey: String)
