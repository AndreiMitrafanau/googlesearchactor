package actors

import akka.actor.Actor
import models.{History, RememberString, Remind}

class HistoryActor extends Actor {
  private var queries = Seq[String]()

  override def receive = {
    case Remind => sender ! History(queries)
    case RememberString(toRemember: String) => queries :+= toRemember
  }
}