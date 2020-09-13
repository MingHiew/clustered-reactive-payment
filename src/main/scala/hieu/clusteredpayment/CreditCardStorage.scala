package hieu.clusteredpayment

import java.util.UUID

import akka.actor._
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.persistence.PersistentActor
import hieu.clusteredpayment.Configuration.{CreditCardId, UserId}

object CreditCardStorage {

  // the protocol for adding credit cards
  final case class AddCreditCard(userId: UserId, last4Digits: String, replyTo: ActorRef)
  final case class CreditCardAdded(id: CreditCardId, userId: UserId, last4Digits: String)

  sealed trait AddCreditCardResult
  case class Added(id: CreditCardId) extends AddCreditCardResult
  case object Duplicate extends AddCreditCardResult

  // the protocol for looking up credit cards by credit card id
  final case class FindById(id: CreditCardId, replyTo: ActorRef)
  case class CreditCardFound(card: StoredCreditCard)
  case class CreditCardNotFound(id: CreditCardId)
  case class CreditCardExists(id: UserId,last4Digits: String)

  case class StoredCreditCard(id: CreditCardId, userId: UserId, last4Digits: String)

}

class CreditCardStorage extends PersistentActor with ActorLogging {
  import CreditCardStorage._

  var cards: Map[CreditCardId, StoredCreditCard] = Map.empty

  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = {
    case AddCreditCard(userId,last4Digits,replyTo) => {
      val cardAlreadyExists = cards.values.exists(cc => cc.userId == userId && cc.last4Digits == last4Digits)
      if(cardAlreadyExists) {
        log.warning("Tried adding already existing card")
        replyTo ! CreditCardExists(userId,last4Digits)
      } else {
        val creditCardId = CreditCardId(UUID.randomUUID().toString)
        persist(CreditCardAdded(creditCardId, userId, last4Digits)) {_=>
          cards += creditCardId -> StoredCreditCard(creditCardId,userId,last4Digits)
        }
      }
    }
    case FindById(id,replyTo) if cards.contains(id) => {
      replyTo ! CreditCardFound(cards(id))
    }
    case FindById(id,replyTo) if !cards.contains(id) => {
      replyTo ! CreditCardNotFound(id)
    }
  }

  override def persistenceId: String = "credit-card-storage"
}