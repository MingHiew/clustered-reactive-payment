package hieu.clusteredpayment

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, SupervisorStrategy}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.cluster.typed.{ClusterSingleton, SingletonActor}
import com.typesafe.config.ConfigFactory

/**
 * Top-level actor of the Payment hieu.clusteredpayment.Processor implementation.
 *
 * It is responsible for supervising the various components of the processor, which are planned to be the following:
 *
 * - API
 * - Payment Handling
 * - Configuration
 * - Payment Processors
 *   - Credit Card Payment hieu.clusteredpayment.Processor
 *   - Google Pay Payment hieu.clusteredpayment.Processor
 *   - ...
 *
 * An example request flow would be:
 *
 * - call API for credit card payment
 * - API               ->  Payment Handling:               authorization request
 * - Payment Handling  ->  Configuration:                  lookup merchant configuration
 * - Payment Handling  <-  Configuration:                  reference to payment processing service, additional data
 * - Payment Handling  ->  Credit Card Payment Processing: issue authorization using additional data
 * - Payment Handling  <-  Credit Card Payment Processing: authorization success
 * - Payment Handling  <-  API:                            authorization response
 *
 */
class PaymentProcessor(port: Int) extends App{
  val config = ConfigFactory.parseString(
    s"""
       |akka.remote.artery.canonical.port = $port
     """.stripMargin)
    .withFallback(ConfigFactory.load("application.conf"))

  val system = ActorSystem("RTJVMCluster", config)

  val configSingleton = system.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props[Configuration],
      terminationMessage = PoisonPill,
      ClusterSingletonManagerSettings(system)
    ),
    "configuration"
  )

  val creditCardStorageSingleton = system.actorOf(
    ClusterSingletonManager.props(
      singletonProps = Props[CreditCardStorage],
      terminationMessage = PoisonPill,
      ClusterSingletonManagerSettings(system)
    ),
    "credit-card-storage"
  )
}

