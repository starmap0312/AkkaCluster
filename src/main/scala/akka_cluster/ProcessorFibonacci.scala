package akka_cluster

import akka.actor.{Actor, ActorRef, Props}

// 1) Business logic
object ProcessorFibonacci {
  sealed trait ProcessorFibonacciMessage
  case class Compute(n: Int, replyTo: ActorRef) extends ProcessorFibonacciMessage

  def props(nodeId: String) = Props(new ProcessorFibonacci(nodeId))

  def fibonacci(x: Int): BigInt = {
    @annotation.tailrec def fibHelper(x: Int, prev: BigInt = 0, next: BigInt = 1): BigInt = x match {
      case 0 => prev
      case 1 => next
      case _ => fibHelper(x - 1, next, next + prev)
    }
    fibHelper(x)
  }
}

class ProcessorFibonacci(nodeId: String) extends Actor {
  import ProcessorFibonacci._

  override def receive: Receive = {
    case Compute(value, replyTo) => {
      // once the Compute message is received, do the computation and return the result to the sender
      replyTo ! ProcessorResponse(nodeId, fibonacci(value))
    }
  }
}
