package com.wixpress.mapsites
import com.fasterxml.jackson.databind.DeserializationFeature
import com.wixpress.authorization.MetaSiteId
import com.wixpress.hoopoe.ids.Guid
import com.wixpress.hoopoe.json.JsonMapper
import com.workday.esclient.EsClient
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

class ElasticSearchDao(indexName: String, typeName: String) extends Dao{

  val esUrl = "http://localhost:9200"
  val client = EsClient.createEsClient(esUrl)
  val generatedIndexName = indexName + Math.random() //generates new index in each run
  client.createIndex(generatedIndexName) //creates index in ES

  val mapper = JsonMapper.objectMapperFromTemplate
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
  def deserialize(payload: Array[Byte]): Option[Address] = {
    Some(mapper.readValue(payload, classOf[Address]))
  }


  override def delete(metasiteId: Guid[_]): Unit = ??? //implement if needed


  override def get(metaSiteId: Guid[_]): Option[Address] = {
    val getDoc = client.get(generatedIndexName, metaSiteId.getId)
    val jsonString = getDoc.get.sourceJson.get.toString
    val addressFromJson: Option[Address]= deserialize(jsonString.getBytes)
    addressFromJson
  }

  override def put(metaSiteId: MetaSiteId, address: Address): Unit = {
    implicit val formats = DefaultFormats
    val jsonString = write(address)
    val id = metaSiteId.getId
    client.index(generatedIndexName, typeName, id, jsonString)
  }

}
