package com.wixpress.mapsites

import com.wix.bootstrap.jetty.BootstrapServer


object MapSitesServer extends BootstrapServer {

  override def additionalSpringConfig = Some(classOf[MapSitesConfig])

  override val programName: String = "Map Sites Server"
}
