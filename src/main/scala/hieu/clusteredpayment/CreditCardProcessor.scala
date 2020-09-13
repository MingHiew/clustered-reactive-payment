package hieu.clusteredpayment

import java.time.LocalDateTime
import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash}
import hieu.clusteredpayment.Configuration._
import squants.market.Money

class CreditCardProcessor(creditCardStorage: CreditCardStorage) extends Actor
  with ActorLogging with Stash {
  import CreditCardProcessor._
  //register with the receptionist

  override def receive: Receive = handleRequest

  def handleRequest: Receive = {
      case request @ CreditCardProcessor.Process(_, _, _, paymentMethod: CreditCard, _) => {
        creditCardStorage.self ! CreditCardStorage.FindById(paymentMethod.storageId, _)
        context.become(retrievingCard(request))
      }
      case _ => unhandled(_)
  }

  def retrievingCard(request: CreditCardProcessor.Process): Receive = {
    case CreditCardStorage.CreditCardFound => {
      request.sender ! RequestProcessed(
        Transaction(
          TransactionId(UUID.randomUUID().toString),
          LocalDateTime.now,
          request.amount,
          request.userId,
          request.merchantConfiguration.merchantId))

      // we're able to process new requests
      unstashAll()
      context.become(handleRequest)
    }
  }

}

object CreditCardProcessor {
  sealed trait ProcessorRequest
  final case class Process(
                            amount: Money,
                            merchantConfiguration: MerchantConfiguration,
                            userId: UserId,
                            paymentMethod: PaymentMethod,
                            sender: ActorRef)
    extends ProcessorRequest

  sealed trait ProcessorResponse
  final case class RequestProcessed(transaction: Transaction) extends ProcessorResponse

  final case class Transaction(
                                id: TransactionId,
                                timestamp: LocalDateTime,
                                amount: Money,
                                userId: UserId,
                                merchantId: MerchantId)
}
