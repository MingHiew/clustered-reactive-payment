package hieu.clusteredpayment

import akka.actor.typed.scaladsl.{Behaviors, Routers}
import akka.actor.typed.{ActorRef, Behavior}
import akka.cluster.sharding.typed.HashCodeNoEnvelopeMessageExtractor
import akka.cluster.sharding.typed.scaladsl.{ClusterSharding, Entity, EntityTypeKey}
import akka.persistence.typed.PersistenceId
import hieu.clusteredpayment.Configuration._
import squants.market.Money

/**
 * Keeps track of available payment processors and delegates incoming requests to a dedicated sharded entity
 */
object PaymentHandling {

  // ~~~ public protocol
  sealed trait Command

  case class HandlePayment(
                            orderId: OrderId,
                            amount: Money,
                            merchantId: MerchantId,
                            userId: UserId,
                            sender: ActorRef[PaymentRequestHandler.Response])
    extends Command
}
