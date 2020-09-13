package hieu.clusteredpayment

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, SupervisorStrategy}
import akka.cluster.typed.{ClusterSingleton, SingletonActor}

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
object PaymentProcessor {

}
