package endpoints

import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import de.heikoseeberger.akkahttpcirce.ErrorAccumulatingCirceSupport._
import models._
import models.repository.OrderRepository

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class OrderEndpoint(repository: OrderRepository)(implicit ec: ExecutionContext, mat: Materializer) {
  val routes =
    pathPrefix("api" / "orders") {
      (get & path("next")) {
        onComplete(repository.next()) {
          case Success(Some(order)) =>
            complete(Marshal(order).to[ResponseEntity].map { e => HttpResponse(entity = e) })
          case Success(None)        =>
            complete(HttpResponse(status = StatusCodes.NotFound))
          case Failure(e)           =>
            complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map { e => HttpResponse(entity = e, status = StatusCodes.InternalServerError) })
        }
      } ~ (get & path(Segment).as(FindByIdRequest)) { request =>
        onComplete(repository.findById(request.id)) {
          case Success(Some(order)) =>
            complete(Marshal(order).to[ResponseEntity].map { e => HttpResponse(entity = e) })
          case Success(None)        =>
            complete(HttpResponse(status = StatusCodes.NotFound))
          case Failure(e)           =>
            complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map { e => HttpResponse(entity = e, status = StatusCodes.InternalServerError) })
        }
      } ~ (get & pathEndOrSingleSlash) {
        onComplete(repository.all()) {
          case Success(orders) =>
            complete(Marshal(orders).to[ResponseEntity].map { e => HttpResponse(entity = e) })
          case Failure(e)           =>
            complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map { e => HttpResponse(entity = e, status = StatusCodes.InternalServerError) })
        }
      } ~ (post & pathEndOrSingleSlash & entity(as[Order])) { order =>
        onComplete(repository.save(order)) {
          case Success(id) =>
            complete(HttpResponse(status = StatusCodes.Created, headers = List(Location(s"/api/orders/$id"))))
          case Failure(e)  =>
            complete(Marshal(Message(e.getMessage)).to[ResponseEntity].map { e => HttpResponse(entity = e, status = StatusCodes.InternalServerError) })
        }
      }
    }
}
