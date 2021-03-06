package sprest.examples.slick

import sprest.slick.SlickPersistence
import sprest.slick.typemappers._
import sprest.Formats._

object DB extends SlickPersistence {
  import scala.slick.driver.H2Driver
  import scala.slick.driver.H2Driver.simple._
  import scala.slick.session.{ Session => SlickSession, Database }
  import scala.slick.lifted.TypeMapper
  import sprest.models._
  import models._
  //import sprest.examples.slick.security.Session
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext

  object ExampleBackend extends SlickBackend {
    val database = Database.forURL("jdbc:mysql://localhost:3306/reactive-example",
      driver = "org.h2.Driver",
      user = "root",
      password = "root")

    val driver = H2Driver
  }

  val backend = ExampleBackend

  abstract class UnsecuredDAO[M <: sprest.models.Model[Int]] extends TableDAO[M, Int] {

    case class Selector(id: Int) extends UniqueSelector[M, Int]
    override implicit def generateSelector(id: Int) = Selector(id)

    override protected def addImpl(m: M)(implicit ec: ExecutionContext): Future[M] = futureWithSession { implicit s =>
      table.insert(m)
      m
    }
    override protected def allImpl(implicit ec: ExecutionContext): Future[Iterable[M]] = futureWithSession { implicit s => Query(table).list }
    override def findBySelector(selector: Selector)(implicit ec: ExecutionContext): Future[Option[M]] = futureWithSession { implicit s => table.byId(selector.id).firstOption }
    override def remove(selector: Selector)(implicit ec: ExecutionContext) = withSession { implicit s => table.byId(selector.id).mutate(_.delete) }
    override protected def updateImpl(m: M)(implicit ec: ExecutionContext) = futureWithSession { implicit s =>
      table.byId(m.id.get).mutate(_.row = m)
      m
    }

  }

  //val tm = TypeMapper
  implicit object DateTimeMapper

  // MySQL tables:
  class ToDosTable[M <: Model[Int], Int](name: String) extends ModelTable[M, Int](name) {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def * = column[M]("*")
  }
  object ToDos extends UnsecuredDAO[ToDo] with IntId {
    override def table: ModelTable[ToDo, Int] = {
      new ToDosTable[ToDo, Int]("todo")
    }
  }

  class RemindersTable[M <: Model[Int], Int](name: String) extends ModelTable[M, Int](name) {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def * = column[M]("*")
  }
  object Reminders extends UnsecuredDAO[Reminder] with IntId {
    override def table: ModelTable[Reminder, Int] = {
      new RemindersTable[Reminder, Int]("reminder")
    }
  }
}

