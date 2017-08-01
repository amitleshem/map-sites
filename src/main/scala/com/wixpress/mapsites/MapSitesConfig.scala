package com.wixpress.mapsites

import com.wixpress.greyhound.{Consumers, GreyhoundConsumerSpec, GreyhoundSpringConfig, MessageHandler}
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import org.springframework.context.annotation.{Bean, Configuration, Import}


@Configuration
@Import(Array(classOf[GreyhoundSpringConfig]))
class MapSitesConfig {


  @Bean def dao: Dao = new InMemoryDb

  @Bean def mapSitesController(dao: Dao): MapSitesController = new MapSitesController(dao)

  @Bean def eventMessageHandler(dao: Dao): EventMessageHandler = new EventMessageHandler(dao)

  @Bean def consumer(consumers: Consumers, eventMessageHandler: EventMessageHandler) = {
    val messageHandler = MessageHandler.aMessageHandler {
      sitePropertyNotification: SitePropertiesNotification => eventMessageHandler.handleMessage(sitePropertyNotification)
    }.build
    consumers.add(GreyhoundConsumerSpec.aGreyhoundConsumerSpec(topic = "site-properties.changes", messageHandler = messageHandler)
      .withGroup("myGroup"))
    messageHandler
  }
}
