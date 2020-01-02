package akka_cluster

import akka.actor.typed._
import akka.actor.typed.scaladsl._
import akka.cluster.ClusterEvent._
import akka.cluster.MemberStatus

import akka.actor.typed.scaladsl.adapter._
import akka.cluster.typed._

object TypedClusterExample extends App {
  val classicSystem = akka.actor.ActorSystem("ClusterExample")
  implicit val system: ActorSystem[Nothing] = classicSystem.toTyped

  val cluster = Cluster(system)

  system.terminate()
}
