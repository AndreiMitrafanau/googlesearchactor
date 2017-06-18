package controllers

import actors.{HistoryActor, AskGoogleActor}
import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import com.google.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.libs.ws.WSClient
import play.api.mvc.{Controller, _}

class Application @Inject()(implicit system: ActorSystem,
                            materializer: Materializer,
                            ws: WSClient) extends Controller {

  private val historyActor = system.actorOf(Props(classOf[HistoryActor]))

  def index = Action {
    Ok(views.html.index("WebSocket Test"))
  }

  def ask = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef(out => AskGoogleActor.props(out, historyActor, ws))
  }

}