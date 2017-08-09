package com.wixpress.mapsites

import java.nio.file.{Files, Paths}

import com.wixpress.framework.remoting.HttpConstants
import com.wixpress.framework.rpc.client.{RpcOverHttpClientEventHandler, RpcOverHttpRequestContext, RpcOverHttpResponseContext, WixAsyncRpcOverHttpClientFactory}
import com.wixpress.framework.rpc.discovery.{RpcProxyFactory, StaticRpcOverHttpProxyFactory}
import com.wixpress.framework.rpc.json.JsonRpcProtocolClient
import com.wixpress.hoopoe.json.JsonMapper

object RpcFactory {

  def withSession = {
    val wixSession2 = {
      val file = Paths.get(System.getProperty("user.home"), ".wixsession2")
      if (Files.exists(file)) Files.readAllLines(file).get(0) else ""
    }

    val rpcFactory: RpcProxyFactory = {
      def staticSessionWriter = new RpcOverHttpClientEventHandler {
        def postInvoke(requestContext: RpcOverHttpRequestContext, responseContext: RpcOverHttpResponseContext): Unit = {}

        def preInvoke(context: RpcOverHttpRequestContext): Unit = {
          context.addHeader(HttpConstants.NEW_SESSION_HEADER, wixSession2)
        }
      }

      val protocol = new JsonRpcProtocolClient(JsonMapper.global)
      new StaticRpcOverHttpProxyFactory(new WixAsyncRpcOverHttpClientFactory(None), protocol, staticSessionWriter)
    }
    rpcFactory
  }

}