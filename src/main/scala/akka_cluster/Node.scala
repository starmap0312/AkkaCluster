package akka_cluster

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.FromConfig

// 3) Node (Service)
object Node {

  sealed trait NodeMessage

  case class GetFibonacci(n: Int)

  case object GetClusterMembers

  def props(nodeId: String) = Props(new Node(nodeId))
}

class Node(nodeId: String) extends Actor {

  import Node._
  import ClusterManager.GetMembers
  import Processor.ComputeFibonacci

  val processor: ActorRef = context.actorOf(Processor.props(nodeId), "processor") // the routee actors that will receive the messages are of path /user/node/processor
  val processorRouter: ActorRef = context.actorOf(FromConfig.props(Props.empty), "processorRouter") // the router actor is of path /node/processorRouter
  val clusterManager: ActorRef = context.actorOf(ClusterManager.props(nodeId), "clusterManager")

  override def receive: Receive = {
    case GetClusterMembers => clusterManager forward GetMembers
    case GetFibonacci(value) => processorRouter forward ComputeFibonacci(value)
  }
}
