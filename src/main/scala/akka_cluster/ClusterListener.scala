package akka_cluster

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

// 2) Cluster management
object ClusterListener {
  def props(nodeId: String, cluster: Cluster) = Props(new ClusterListener(nodeId, cluster))
}

class ClusterListener(nodeId: String, cluster: Cluster) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    // before starting, it subscribes itself to the event messages of the cluster
    // therefore, it will receive the event messages of the cluster, ex. MemberUp, UnreachableMember, etc.
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    // when stopped, it unsubscribes itself from the cluster events
    cluster.unsubscribe(self)
  }

  def receive = {
    // taking actions on cluster events by logging the events
    case MemberUp(member) =>
      log.info("Node {} - Member is Up: {}", nodeId, member.address)
    case UnreachableMember(member) =>
      log.info(s"Node {} - Member detected as unreachable: {}", nodeId, member)
    case MemberRemoved(member, previousStatus) =>
      log.info(s"Node {} - Member is Removed: {} after {}", nodeId, member.address, previousStatus)
    case _: MemberEvent => // ignore
  }
}
