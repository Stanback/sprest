package sprest.examples.reactivemongo

import sprest.reactivemongo.{ ReactiveMongoPersistence, BsonProtocol }
import sprest.reactivemongo.typemappers._
import sprest.Formats._
import spray.json.RootJsonFormat

object DB extends ReactiveMongoPersistence {

  import reactivemongo.api._
  import sprest.models.UUIDStringId
  import sprest.models.UniqueSelector
  import models._
  import sprest.examples.reactivemongo.security.Session
  import scala.concurrent.ExecutionContext

  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  lazy val db = connection("reactive-example")(Main.system.dispatcher)

  // Json mapping to / from BSON - in this case we want "_id" from BSON to be 
  // mapped to "id" in JSON in all cases
  implicit object JsonTypeMapper extends SprayJsonTypeMapper with NormalizedIdTransformer

  abstract class UnsecuredDAO[M <: sprest.models.Model[String]](collName: String)(implicit jsformat: RootJsonFormat[M]) extends CollectionDAO[M, String, Session](db(collName)) {

    case class Selector(id: String, session: Option[Session]) extends UniqueSelector[M, String, Session]

    override implicit def generateSelector(id: String, maybeSession: Option[Session]) = Selector(id, maybeSession)
    override protected def addImpl(m: M)(implicit maybeSession: Option[Session], ec: ExecutionContext) = doAdd(m)
    override protected def updateImpl(m: M)(implicit maybeSession: Option[Session], ec: ExecutionContext) = doUpdate(m)
    override def remove(selector: Selector)(implicit maybeSession: Option[Session], ec: ExecutionContext) = uncheckedRemoveById(selector.id)
  }

  // MongoDB collections:
  object ToDos extends UnsecuredDAO[ToDo]("todos") with UUIDStringId
  object Reminders extends UnsecuredDAO[Reminder]("reminders") with UUIDStringId

}

