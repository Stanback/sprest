package sprest.examples.slick

import spray.routing.SimpleRoutingApp
import sprest.routing.RestRoutes

trait Routes extends RestRoutes { this: SimpleRoutingApp with spray.httpx.TwirlSupport =>
  import spray.routing.Directives._
  import spray.httpx.SprayJsonSupport._
  import spray.httpx.encoding.Gzip
  import spray.json._

  type SessionImpl = security.Session

  def maybeSession = provide(Some(security.Session()))

  val js = pathPrefix("js" / Rest) { fileName =>
    get {
      encodeResponse(Gzip) { getFromResource(s"js/$fileName") }
    }
  }

  val css = pathPrefix("css" / Rest) { fileName =>
    get {
      getFromResource(s"css/$fileName")
    }
  }

  val index = path("") {
    get {
      complete {
        html.index.render("Hello, Spray!")
      }
    }
  }

  val bootstrap = pathPrefix("bootstrap" / Rest) { fileName =>
    get {
      getFromResource(s"twitter/bootstrap/$fileName")
    }
  }

  val publicAssets = js ~ css ~ bootstrap

  val api = pathPrefix("api") {
    dynamic(restInt("todos", DB.ToDos) ~
      restInt("reminders", DB.Reminders))
  }

  val routes = index ~ publicAssets ~ api
}
