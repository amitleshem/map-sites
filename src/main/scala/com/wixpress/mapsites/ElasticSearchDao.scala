package com.wixpress.mapsites
import com.fasterxml.jackson.databind.DeserializationFeature
import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.hoopoe.json.JsonMapper
import com.wixpress.siteproperties.api.v2.GeoCoordinates
import com.workday.esclient.EsClient
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.util.Try



class ElasticSearchDao(esUrl: String, indexName: String) extends Dao{

  val client = EsClient.createEsClient(esUrl)

  Try{client.catIndex(indexName)}.recover{ case _ => client.createIndex(indexName, Some(ElasticSearchDao.settings)) }.get

  override def delete(metasiteId: Guid[_]): Unit = ??? //implement if needed

  override def get(metaSiteId: Guid[_]): Option[Address] = {
    val getDoc = client.get(indexName, metaSiteId.getId)
    getDoc.get.sourceJson
      .map(_.toString.getBytes)
      .map(ElasticSearchDao.deserialize)
      .map(ElasticSearchDao.esAddressToAddress)
  }

  override def put(metaSiteId: MetaSiteId, address: Address): Unit = {
    implicit val formats = DefaultFormats
    val jsonString = write(ElasticSearchDao.convertToES(address))
    val id = metaSiteId.getId
    client.index(indexName, ElasticSearchDao.typeName, id, jsonString)
  }

}



object ElasticSearchDao{

  val typeName = "site"
  val settings = Map("mappings" -> Map("site" -> Map("properties" -> Map("coordinates" -> Map("type" -> "geo_point")))))

  val mapper = JsonMapper.objectMapperFromTemplate
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  private def deserialize(payload: Array[Byte]): ESAddress =
  ElasticSearchDao.mapper.readValue(payload, classOf[ESAddress])

  private def esGeoPointToGeoCoordinates(eSGeoPoint: ESGeoPoint) = GeoCoordinates(eSGeoPoint.lat, eSGeoPoint.lon)
  private def geoCoordinatesToESGeoPoint(c: GeoCoordinates) = ESGeoPoint(c.latitude, c.longitude)
  private def esAddressToAddress(eSAddress: ESAddress): Address =
    Address(eSAddress.street, eSAddress.city, eSAddress.country, eSAddress.coordinates.map(esGeoPointToGeoCoordinates))
  private def convertToES(address: Address): ESAddress =
    ESAddress(address.street, address.city, address.country, address.coordinates.map(geoCoordinatesToESGeoPoint))

}

case class ESGeoPoint(lat: Double, lon: Double)
case class ESAddress(street: String, city: String, country: Int, val coordinates: Option[ESGeoPoint])