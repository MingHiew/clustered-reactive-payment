package hieu.clusteredpayment

import akka.actor._
import akka.persistence.PersistentActor
import akka.persistence.typed.scaladsl.{Effect, EventSourcedBehavior}
import akka.persistence.typed.{PersistenceId, RecoveryCompleted}
import hieu.clusteredpayment.Configuration.{CreditCard, MerchantId, OrderId, TransactionId, UserId}
import squants.market.Money

/**
 * Handler for a particular payment request. It interacts with the configuration actor and a payment processing actor.
 */
object PaymentRequestHandler {
// public protocol
  sealed trait Command {
    def orderId: OrderId
  }

  final case class HandlePaymentRequest(
                                         orderId: OrderId,
                                         amount: Money,
                                         merchantId: MerchantId,
                                         userId: UserId,
                                         replyTo: ActorRef)
    extends Command

  final case object GracefulStop extends Command {
    // this message is intended to be sent directly from the parent shard, hence the orderId is irrelevant
    override def orderId: OrderId = OrderId("")
  }

  sealed trait Event

  final case class PaymentRequestReceived(
                                           orderId: OrderId,
                                           amount: Money,
                                           merchantId: MerchantId,
                                           userId: UserId,
                                           replyTo: ActorRef)
    extends Event

  final case class PaymentRequestProcessed(transactionId: TransactionId) extends Event

  sealed trait State

  final case object Empty extends State

  final case class ProcessingPayment(
                                      client: ActorRef,
                                      orderId: OrderId,
                                      amount: Money,
                                      merchantId: MerchantId,
                                      userId: UserId)
    extends State

  final case class PaymentProcessed(
                                     client: ActorRef,
                                     transactionId: TransactionId,
                                     orderId: OrderId,
                                     amount: Money,
                                     merchantId: MerchantId,
                                     userId: UserId)
    extends State

  sealed trait Response

  final case class PaymentAccepted(transactionId: TransactionId) extends Response

  final case class PaymentRejected(reason: String) extends Response

}

class PaymentRequestHandler extends PersistentActor with ActorLogging{
  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = ???

  override def persistenceId: String = ???
}