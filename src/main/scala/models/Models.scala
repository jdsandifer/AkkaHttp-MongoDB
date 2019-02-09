package models

import io.circe._
import io.circe.syntax._
import org.bson.types.ObjectId

case class FindByIdRequest(id: String) {
  require(ObjectId.isValid(id), "the informed id is not a representation of a valid hex string")
}

case class Order(_id: ObjectId, customerName: String, drink: String, quantity: Int) {
  require(customerName != null, "customerName not informed")
  require(customerName.nonEmpty, "customerName cannot be empty")
  require(drink != null, "drink not informed")
  require(drink.nonEmpty, "drink cannot be empty")
  require(quantity > 0, "age cannot be lower than 1")
}

object Order {
  implicit val encoder: Encoder[Order] = (a: Order) => {
    Json.obj(
      "id" -> a._id.toHexString.asJson,
      "customerName" -> a.customerName.asJson,
      "drink" -> a.drink.asJson,
      "quantity" -> a.quantity.asJson
    )
  }

  implicit val decoder: Decoder[Order] = (c: HCursor) => {
    for {
      customerName <- c.downField("customerName").as[String]
      drink <- c.downField("drink").as[String]
      quantity <- c.downField("quantity").as[Int]
    } yield Order(ObjectId.get(), customerName, drink, quantity)
  }
}

case class Message(message: String)

object Message {
  implicit val encoder: Encoder[Message] = m => Json.obj("message" -> m.message.asJson)
}
