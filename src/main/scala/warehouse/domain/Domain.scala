package warehouse.domain

import java.sql.{Blob, Timestamp}

case class Item(id:Option[Int], reference:String, quantity:Int, packages:Option[String], picture:Option[Array[Byte]], createdAt:Option[Timestamp])
