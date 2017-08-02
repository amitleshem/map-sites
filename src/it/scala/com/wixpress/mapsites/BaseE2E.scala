package com.wixpress.mapsites

import com.wix.bootstrap.BootstrapManagedService
import com.wixpress.framework.test.env.{Configurer, GlobalTestEnvSupport, TestEnv, TestEnvBuilder}
import com.wixpress.greyhound.{GreyhoundTestingSupport, KafkaDriver, KafkaManagedService}
import com.wixpress.hoopoe.config.TestConfigFactory.aTestEnvironmentFor
import com.wixpress.siteproperties.testkit.SitePropertiesTestKit

/**
  * Created by amitle on 31/07/2017.
  */
trait BaseE2E extends GlobalTestEnvSupport with GreyhoundTestingSupport {
  override def testEnv: TestEnv = EmbeddedEnvironment.testEnv
}

object EmbeddedEnvironment {

  val testKit = SitePropertiesTestKit.createAndStart(8088)


  def serviceConfiguration = {
    aTestEnvironmentFor[ConfigRoot]("map-sites",
      ("rpc_service_url.com.wixpress.siteproperties.site-properties-service", s"http://localhost:8088")
    )
  }

  KafkaDriver.getInstance()

  val testEnv: TestEnv = TestEnvBuilder()
    .withMainService(BootstrapManagedService(MapSitesServer))
    .withCollaborators(KafkaManagedService("site-properties.changes"))
    .withConfigurer(Configurer(serviceConfiguration))
    .build()

}