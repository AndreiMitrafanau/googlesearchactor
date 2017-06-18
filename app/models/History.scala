package models

case class History(queries: Seq[String])
case object Remind
case class RememberString(toRemember: String)