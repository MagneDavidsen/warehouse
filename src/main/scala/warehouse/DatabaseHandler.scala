package warehouse

import java.sql.Timestamp
import java.util.Date
import javax.sql.DataSource

import warehouse.domain.Item

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.session.Database
import scala.slick.session.Database.threadLocalSession

trait DataSourceComponent{
  val dataSource: DataSource
}

trait DatabaseHandlerComponent{this: DataSourceComponent =>

  val databaseHandler: DatabaseHandler

  class DatabaseHandler {
    object ItemObject {
      def fromRow(id: Option[Int], reference: String, quantity: Int, packages: Option[String],
                  picture: String, createdAt: Option[Timestamp]): Item = Item(id, reference, quantity, packages, picture, createdAt)

      def toRow(i: Item) = Some(i.id, i.reference, i.quantity, i.packages, i.picture, i.createdAt)
    }

    object Items extends Table[Item]("items") {
      def id = columnToOptionColumn(column[Int]("id", O.PrimaryKey, O.AutoInc))
      def reference = column[String]("reference")
      def quantity = column[Int]("quantity")
      def packages = column[Option[String]]("packages")
      def picture = column[String]("picture", O.DBType("TEXT"))
      def createdAt = column[Option[Timestamp]]("created_at")

      def * = id ~ reference ~ quantity ~ packages ~ picture ~ createdAt <>(ItemObject.fromRow _, ItemObject.toRow _)
      def forInsert = reference ~ quantity ~ packages ~ picture ~ createdAt <>
        ( { t => Item(None, t._1, t._2, t._3, t._4, t._5)}, { (i: Item) => Some((i.reference, i.quantity, i.packages, i.picture, i.createdAt))})
    }

    def getItem(itemId: Int): Option[Item] = {
      Database.forDataSource(dataSource) withSession {
        val query = for {
          i <- Items if i.id === itemId
        } yield i
        query.firstOption
      }
    }

    def getItems(): List[Item] = {
      Database.forDataSource(dataSource) withSession {
        val query = for {
          i <- Items
        } yield i
        query.list
      }
    }

    def createItem(item: Item): Item = {
      val now = new Timestamp(new Date().getTime)
      Database.forDataSource(dataSource) withSession {
        val itemId = Items.forInsert returning Items.id insert Item(None, item.reference, item.quantity, item.packages, item.picture, Some(now))
        Item(itemId, item.reference, item.quantity, item.packages, item.picture, Some(now))
      }
    }
  }
}