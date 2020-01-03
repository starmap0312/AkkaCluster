package akka_cluster

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

// 0) Server (API): every node of our cluster runs a server able to receive requests
object Server extends App with NodeRoutes {

  implicit val system: ActorSystem = ActorSystem("cluster-playground")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val config: Config = ConfigFactory.load()
  val address = config.getString("http.ip")
  val port = config.getInt("http.port")
  val nodeId = config.getString("clustering.ip")

  val node: ActorRef = system.actorOf(Node.props(nodeId), "node")

  lazy val routes: Route = healthRoute ~ statusRoutes ~ processRoutes

  Http().bindAndHandle(routes, address, port)
  println(s"Node $nodeId is listening at http://$address:$port")
  // http://127.0.0.1:8000/health
  //   OK
  // http://127.0.0.1:8000/status/members
  //   ["akka://cluster-playground@127.0.0.1:2552"]
  // http://127.0.0.1:8000/process/fibonacci/10
  //   { nodeId: "127.0.0.1", result: 55 }
  Await.result(system.whenTerminated, Duration.Inf)
}
