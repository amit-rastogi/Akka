package com.samples
import akka.actor.{Actor, ActorSystem, Props}
import com.samples.Counter.Counter.{Decrement, Increment, Print}

object Counter extends App{

  /*A Counter actor. Would respond to Increment, Decrement and Print messages*/

  //Domain of Counter having messages supported by Counter actor
  object Counter {
    case object Increment
    case object Decrement
    case object Print
  }

  class Counter extends Actor {
    var counter: Int = 0
    override def receive: Receive = {
      case Increment => counter += 1
      case Decrement => counter -= 1
      case Print => println(s"[Counter Actor] - Value of counter is $counter")
    }
  }

  //create an akka actor system
  val actorSystem = ActorSystem("CounterSample")

  //create a counter actor
  val counter = actorSystem.actorOf(Props[Counter], "counter")

  //send messages to counter
  counter ! Increment
  counter ! Increment
  counter ! Decrement
  counter ! Print

  //terminate our actor system
  actorSystem.terminate()
}
