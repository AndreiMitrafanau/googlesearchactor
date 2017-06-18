package actors

import akka.actor.{Actor, ActorRef, Props, _}
import models.{History, RememberString, Remind}
import play.api.libs.ws.WSClient

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AskGoogleActor {
  def props(out: ActorRef, handler: ActorRef, ws: WSClient) =
    Props(new AskGoogleActor(out, handler, ws))
}

class AskGoogleActor(out: ActorRef, handler: ActorRef, ws: WSClient) extends Actor {

  def receive = {
    case h: History => out ! h.queries.mkString(" ")
    case "history" => handler ! Remind
    case query: String =>
      handler ! RememberString(query)
      out ! askGoogle(query)
    case _ => out ! "Something wrong happened!"
  }

  def askGoogle(query: String): String = {
    implicit val defaultContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
    val response = ws.url(s"https://www.google.by/search?q=$query").get()
    val result = Await.result(response, Duration.Inf).body
    val pattern = "<h3 class=\"r\"><a href=.*<.a>".r
    pattern.findFirstIn(result).getOrElse("Nothing found!")
  }

  override def postStop() = println("Disconnected.")
}

