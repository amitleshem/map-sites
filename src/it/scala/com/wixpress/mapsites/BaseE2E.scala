package com.wixpress.mapsites

import com.fasterxml.jackson.databind.DeserializationFeature
import com.wix.bootstrap.BootstrapManagedService
import com.wixpress.framework.test.env.{Configurer, GlobalTestEnvSupport, TestEnv, TestEnvBuilder}
import com.wixpress.greyhound.{GreyhoundTestingSupport, KafkaDriver, KafkaManagedService}
import com.wixpress.hoopoe.RandomTestUtils
import com.wixpress.hoopoe.config.TestConfigFactory.aTestEnvironmentFor
import com.wixpress.hoopoe.json.JsonMapper
import com.wixpress.siteproperties.testkit.SitePropertiesTestKit

trait BaseE2E extends GlobalTestEnvSupport with GreyhoundTestingSupport {
  override def testEnv: TestEnv = EmbeddedEnvironment.testEnv

  val objectMapper = JsonMapper.objectMapperFromTemplate
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

object EmbeddedEnvironment {

  val testKit = SitePropertiesTestKit.createAndStart(8088)
  val indexName = "sites-" + RandomTestUtils.randomStr.toLowerCase

  def serviceConfiguration = {
    aTestEnvironmentFor[ConfigRoot]("map-sites",
      (config: ConfigRoot) => config.copy(esIndex = indexName),
      ("service_url.com.wixpress.siteproperties.site-properties-service", s"http://localhost:8088")
    )
  }

  KafkaDriver.getInstance()

  val testEnv: TestEnv = TestEnvBuilder()
    .withMainService(BootstrapManagedService(MapSitesServer))
    .withCollaborators(KafkaManagedService("site-properties.changes"))
    .withConfigurer(Configurer(serviceConfiguration))
    .build()

}