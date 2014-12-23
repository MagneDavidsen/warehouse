package warehouse.domain

import java.sql.{Blob, Timestamp}

case class Item(id:Option[Int], reference:String, quantity:Int, packages:Option[String], picture:String, createdAt:Option[Timestamp])
