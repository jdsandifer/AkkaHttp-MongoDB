package features

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.fakemongo.async.FongoAsync
import com.mongodb.async.client.MongoDatabase
import endpoints._
import models._
import models.repository._
import mongodb.Mongo
import org.mongodb.scala.MongoCollection
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, Matchers}

class OrderEndpointFeature
  extends FeatureSpec
    with Matchers
    with ScalatestRouteTest
    with BeforeAndAfterAll  {

  val db: MongoDatabase = {
    val fongo = new FongoAsync("akka-http-mongodb-microservice")
    val db = fongo.getDatabase("AkkaDb")
    db.withCodecRegistry(Mongo.codecRegistry)
  }

  val repository = new OrderRepository(MongoCollection(db.getCollection("col", classOf[Order])))

  val routes = Route.seal(new OrderEndpoint(repository).routes)

  val httpEntity: (String) => HttpEntity.Strict = (str: String) => HttpEntity(ContentTypes.`application/json`, str)

  feature("Drink API") {
    scenario("POST of valid order creates new order") {
      val validOrder =
        """
          {
            "customerName": "Joe Hardy",
            "drink": "Screwdriver",
            "quantity": 2
          }
        """

      Post(s"/api/orders", httpEntity(validOrder)) ~> routes ~> check {
        status shouldBe StatusCodes.Created
      }
    }

    scenario("GET retrieves previously POSTed order") {
      val validOrder =
        """
          {
            "customerName": "Joe Hardy",
            "drink": "Screwdriver",
            "quantity": 2
          }
        """

      Post(s"/api/orders", httpEntity(validOrder)) ~> routes ~> check {
        status shouldBe StatusCodes.Created

        Get(header("Location").orNull.value()) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
    }


    scenario("GET with invalid ID gives BadRequest status") {
      Get(s"/api/orders/1") ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    scenario("POST with no body gives BadRequest status") {
      Post(s"/api/orders", httpEntity("{}")) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    scenario("POST with only customer name gives BadRequest status") {
      val invalidUser =
        """
        {
          "customerName": "Frank Hardy"
        }
        """

      Post(s"/api/orders", httpEntity(invalidUser)) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    scenario("POST with only drink gives BadRequest status") {
      val invalidUser =
        """
        {
          "drink": "Pineapple Juice"
        }
        """

      Post(s"/api/orders", httpEntity(invalidUser)) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }

    scenario("POST with only quantity gives BadRequest status") {
      val invalidUser =
        """
        {
          "quantity": 24
        }
        """

      Post(s"/api/orders", httpEntity(invalidUser)) ~> routes ~> check {
        status shouldBe StatusCodes.BadRequest
      }
    }
  }
}

