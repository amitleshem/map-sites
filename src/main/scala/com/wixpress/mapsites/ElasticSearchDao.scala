package com.wixpress.mapsites
import com.fasterxml.jackson.databind.DeserializationFeature
import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.hoopoe.json.JsonMapper
import com.workday.esclient.EsClient
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

class ElasticSearchDao(esUrl: String, indexName: String) extends Dao{

  val typeName = "site"
  val client = EsClient.createEsClient(esUrl)

  val mapper = JsonMapper.objectMapperFromTemplate
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  private def deserialize(payload: Array[Byte]): Address =
    mapper.readValue(payload, classOf[Address])

  override def delete(metasiteId: Guid[_]): Unit = ??? //implement if needed

  override def get(metaSiteId: Guid[_]): Option[Address] = {
    val getDoc = client.get(indexName, metaSiteId.getId)
    getDoc.get.sourceJson.map(_.toString.getBytes).map(deserialize)
  }

  override def put(metaSiteId: MetaSiteId, address: Address): Unit = {
    implicit val formats = DefaultFormats
    val jsonString = write(address)
    val id = metaSiteId.getId
    client.index(indexName, typeName, id, jsonString)
  }

}
