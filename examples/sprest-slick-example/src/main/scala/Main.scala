package sprest.examples.slick

import akka.actor.ActorSystem
import spray.routing.SimpleRoutingApp
import twirl.api._
import spray.httpx.TwirlSupport
import spray.httpx.encoding.Gzip

object Main extends App
  with SimpleRoutingApp
  with TwirlSupport
  with spray.httpx.SprayJsonSupport
  with Routes {

  override implicit val system = ActorSystem("sprest-slick")

  startServer(interface = "localhost", port = 8080) {
    routes
  }
}

