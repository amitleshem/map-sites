package com.wixpress.mapsites
import com.fasterxml.jackson.databind.DeserializationFeature
import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.hoopoe.json.JsonMapper
import com.wixpress.siteproperties.api.v2.GeoCoordinates
import com.workday.esclient.{EsClient, IndexFieldProperties, IndexMappings, IndexTypeMappings}
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.util.Try

class ElasticSearchDao(esUrl: String, indexName: String) extends Dao{

  val typeName = "site"
  val client = EsClient.createEsClient(esUrl)

  val mapper = JsonMapper.objectMapperFromTemplate
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  val settings = Map("mappings" -> Map("site" -> Map("properties" -> Map("coordinates" -> Map("type" -> "geo_point")))))

  Try{client.catIndex(indexName)}.recover{ case _ => client.createIndex(indexName, Some(settings)) }.get

  private def deserialize(payload: Array[Byte]): ESAddress =
    mapper.readValue(payload, classOf[ESAddress])

  def esGeoPointToGeoCoordinates(eSGeoPoint: ESGeoPoint) = GeoCoordinates(eSGeoPoint.lat, eSGeoPoint.lon)

  private def esAddressToAddress(eSAddress: ESAddress): Address = Address(eSAddress.street, eSAddress.city, eSAddress.country, eSAddress.coordinates.map(esGeoPointToGeoCoordinates))

  override def delete(metasiteId: Guid[_]): Unit = ??? //implement if needed

  override def get(metaSiteId: Guid[_]): Option[Address] = {
    val getDoc = client.get(indexName, metaSiteId.getId)
    getDoc.get.sourceJson
      .map(_.toString.getBytes)
      .map(deserialize)
      .map(esAddressToAddress)
  }

  override def put(metaSiteId: MetaSiteId, address: Address): Unit = {
    implicit val formats = DefaultFormats
    val jsonString = write(convertToES(address))
    val id = metaSiteId.getId
    client.index(indexName, typeName, id, jsonString)
  }

  private def convertToES(address: Address): ESAddress =
    ESAddress(address.street, address.city, address.country, address.coordinates.map(geoCoordinatesToESGeoPoint))

  private def geoCoordinatesToESGeoPoint(c: GeoCoordinates) = ESGeoPoint(c.latitude, c.longitude)

}

case class ESGeoPoint(lat: Double, lon: Double)
case class ESAddress(street: String, city: String, country: Int, val coordinates: Option[ESGeoPoint])