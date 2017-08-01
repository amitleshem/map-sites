package com.wixpress.mapsites

import java.nio.file.{Files, Paths}

import com.wixpress.framework.remoting.HttpConstants
import com.wixpress.framework.rpc.client.{RpcOverHttpClientEventHandler, RpcOverHttpRequestContext, RpcOverHttpResponseContext, WixAsyncRpcOverHttpClientFactory}
import com.wixpress.framework.rpc.discovery.{RpcProxyFactory, StaticRpcOverHttpProxyFactory}
import com.wixpress.framework.rpc.json.JsonRpcProtocolClient
import com.wixpress.greyhound.{Consumers, GreyhoundConsumerSpec, GreyhoundSpringConfig, MessageHandler}
import com.wixpress.hoopoe.json.JsonMapper
import com.wixpress.siteproperties.api.v3.Notifications.SitePropertiesNotification
import com.wixpress.siteproperties.api.v3.SitePropertiesStorageV3
import org.springframework.context.annotation.{Bean, Configuration, Import}
import com.wixpress.hoopoe.config.ConfigFactory._

@Configuration
@Import(Array(classOf[GreyhoundSpringConfig]))
class MapSitesConfig {







  @Bean def dao: Dao = new InMemoryDb

  @Bean def mapSitesController(dao: Dao): MapSitesController = new MapSitesController(dao)

  @Bean def eventMessageHandler(dao: Dao, sitePropertiesStorageV3: SitePropertiesStorageV3): EventMessageHandler = new EventMessageHandler(dao, storage)

  @Bean def consumer(consumers: Consumers, eventMessageHandler: EventMessageHandler) = {
    val messageHandler = MessageHandler.aMessageHandler {
      sitePropertyNotification: SitePropertiesNotification => eventMessageHandler.handleMessage(sitePropertyNotification)
    }.build
    consumers.add(GreyhoundConsumerSpec.aGreyhoundConsumerSpec(topic = "site-properties.changes", messageHandler = messageHandler)
      .withGroup("myGroup"))
    messageHandler
  }





  def staticSessionWriter = new RpcOverHttpClientEventHandler {
    def postInvoke(requestContext: RpcOverHttpRequestContext, responseContext: RpcOverHttpResponseContext): Unit = {}
    def preInvoke(context: RpcOverHttpRequestContext): Unit = {
      context.addHeader(HttpConstants.NEW_SESSION_HEADER, wixSession2)
    }
  }

  val rpcFactory: RpcProxyFactory = {
    val protocol = new JsonRpcProtocolClient(JsonMapper.global)
    new StaticRpcOverHttpProxyFactory(new WixAsyncRpcOverHttpClientFactory(None), protocol, staticSessionWriter)
  }

  lazy val wixSession2 = {
    val file = Paths.get(System.getProperty("user.home"), ".wixsession2")
    if (Files.exists(file)) Files.readAllLines(file).get(0) else ""
  }


  val config = aConfigFor[ConfigRoot]("map-sites")

  lazy val storage = rpcFactory.builderFor[SitePropertiesStorageV3].withBaseUrl(config.services.sitePropertiesUrl).build()

  case class ConfigRoot(services: Services)
  case class Services(sitePropertiesUrl: String)

}
