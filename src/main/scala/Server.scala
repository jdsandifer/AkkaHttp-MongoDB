import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import endpoints._
import models.repository.OrderRepository
import mongodb.Mongo

object Server extends App {
  implicit val system: ActorSystem = ActorSystem("orderApi")
  implicit val executor: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val routes =
    new OrderEndpoint(new OrderRepository(Mongo.orderCollection)).routes

  Http().bindAndHandle(routes, "localhost", 9010).onComplete {
    case Success(b) => println(s"Server is running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
    case Failure(e) => println(s"Could not start application: {}", e.getMessage)
  }
}
