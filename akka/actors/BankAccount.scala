package com.samples

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.samples.BankAccount.BankAccount.{Deposit, Failure, PrintStatement, Success, Withdraw}
import com.samples.BankAccount.Customer.DoTransaction

object BankAccount extends App{

  //A BankAccount actor which responds to Deposit, Withdraw and PrintStatement messages

  //domain of BankAccount having messages supported by BankAccount actor
  object BankAccount {
    case class Deposit(amount: Double)
    case class Withdraw(amount: Double)
    case object PrintStatement
    case class Failure(message: String)
    case class Success(message: String)
  }

  class BankAccount extends Actor {
    var balance: Double = 0
    override def receive: Receive = {
      case Deposit(amount) =>
        if(amount < 0) sender() ! Failure("Cannot deposit negative amount")
        else {
          balance += amount
          sender() ! Success("Deposit successful")
        }
      case Withdraw(amount) =>
        if(amount < 0) sender() ! Failure("Cannot withdraw negative amount")
        else if(amount > balance) sender() ! Failure("Cannot withdraw more than account balance")
        else {
          balance -= amount
          sender() ! Success("Withdraw successful")
      }
      case PrintStatement => println(s"Account balance is $balance")
    }
  }

  //companion object of Customer
  object Customer {
    case class DoTransaction(bankAccount: ActorRef)
    case class Failure(message: String)
    case class Success(message: String)
  }

  class Customer extends Actor{
    override def receive: Receive = {
      case Success(msg) => println(msg)
      case Failure(msg) => println(msg)
      case DoTransaction(bankAccount) => {
        //do some bank transactions
        bankAccount ! Withdraw(1000)
        bankAccount ! Deposit(1000)
        bankAccount ! Withdraw(400)
        bankAccount ! PrintStatement
      }
    }
  }
  //create actor system
  val actorSystem = ActorSystem("BankAccountSample")

  //create a BankAccount actor
  val bankAccount = actorSystem.actorOf(Props[BankAccount], "bankAccount")
  val customer = actorSystem.actorOf(Props[Customer], "customer")

  //ask customer to perform some bank transactions
  customer ! DoTransaction(bankAccount)

  actorSystem.terminate()
}
