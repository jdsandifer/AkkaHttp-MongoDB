package models.repository

import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.{ExecutionContext, Future}
import models.Order

class OrderRepository(collection: MongoCollection[Order])(implicit ec: ExecutionContext) {
  def all(): Future[Seq[Order]] =
    collection
    .find
    .collect
    .head

  def next(): Future[Option[Order]] =
    collection
      .find
      .first
      .head
      .map(Option(_))

  def findById(id: String): Future[Option[Order]] =
    collection
      .find(Document("_id" -> new ObjectId(id)))
      .first
      .head
      .map(Option(_))

  def save(order: Order): Future[String] =
    collection
      .insertOne(order)
      .head
      .map { _ => order._id.toHexString }
}
