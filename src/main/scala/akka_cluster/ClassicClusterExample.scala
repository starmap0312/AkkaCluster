package akka_cluster

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

import scala.util.{Failure, Success}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

class ClassicClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    log.info("preStart")
    //#subscribe
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
    //#subscribe
  }
  override def postStop(): Unit = {
    log.info("postStop")
    cluster.unsubscribe(self)
  }

  def receive = {
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}", member.address, previousStatus)
    case _: MemberEvent => log.info("Unknown MemberEvent")// ignore
  }
}

object ClassicClusterExample extends App {
  implicit val system = ActorSystem()
  import system.dispatcher
  val actorRef = system.actorOf(Props[ClassicClusterListener], "simplecluster")
  implicit val timeout = Timeout(1.seconds)
  val resultFuture = actorRef ? MemberUp

  resultFuture onComplete {
    case Success(result) => println(result)
    case Failure(ex) => println(ex.getMessage)
  }
}
