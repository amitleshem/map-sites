package com.wixpress.mapsites

import com.wix.bootstrap.BootstrapManagedService
import com.wixpress.framework.test.env.{GlobalTestEnvSupport, TestEnv, TestEnvBuilder}
import com.wixpress.greyhound.{GreyhoundTestingSupport, KafkaDriver, KafkaManagedService}

/**
  * Created by amitle on 31/07/2017.
  */
trait BaseE2E extends GlobalTestEnvSupport with GreyhoundTestingSupport {
  override def testEnv: TestEnv = EmbeddedEnvironment.testEnv
}


object EmbeddedEnvironment {
  KafkaDriver.getInstance()

  val testEnv: TestEnv = TestEnvBuilder()
    .withMainService(BootstrapManagedService(MapSitesServer))
    .withCollaborators(KafkaManagedService("site-properties.changes"))
    .build()

}