package hieu.clusteredpayment

import akka.actor._

// the AbstractBehavior trait is the entry point for using the object-oriented style API
class Configuration extends Actor with ActorLogging {
  import Configuration._
  var merchantConfigurations: Map[MerchantId,MerchantConfiguration] = Map.empty
  var userConfigurations: Map[UserId,UserConfiguration] = Map.empty

  override def receive: Receive = {
    case RetrieveConfiguration(merchantId,userId,replyTo) => {
      (merchantConfigurations.get(merchantId),userConfigurations.get(userId)) match {
        case (Some(merchantConfiguration),Some(userConfiguration)) =>
          replyTo ! ConfigurationFound(merchantId,userId,merchantConfiguration,userConfiguration)
        case _ =>
          replyTo ! ConfigurationNotFound(merchantId,userId)
      }
    }
    case StoreMerchantConfiguration(merchantId,configuration,replyTo) => {
      merchantConfigurations += merchantId->configuration
      replyTo ! MerchantConfigurationStored(merchantId)
    }
    case StoreUserConfiguration(userId,configuration,replyTo) => {
      userConfigurations += userId->configuration
      replyTo ! UserConfigurationStored(userId)
    }
  }
}

object Configuration {

  case class OrderId(id: String) extends AnyVal
  case class MerchantId(id: String) extends AnyVal
  case class UserId(id: String) extends AnyVal
  case class BankIdentifier(id: String) extends AnyVal
  case class CreditCardId(id: String) extends AnyVal
  case class TransactionId(id: String) extends AnyVal

  sealed trait PaymentMethod
  case class CreditCard(storageId: CreditCardId) extends PaymentMethod

  case class MerchantConfiguration(merchantId: MerchantId, bankIdentifier: BankIdentifier)
  case class UserConfiguration(paymentMethod: PaymentMethod)

  sealed trait ConfigurationRequest
  final case class RetrieveConfiguration(
                                          merchantId: MerchantId,
                                          userId: UserId,
                                          replyTo: ActorRef)
    extends ConfigurationRequest
  final case class StoreMerchantConfiguration(
                                               merchantId: MerchantId,
                                               configuration: MerchantConfiguration,
                                               replyTo: ActorRef)
    extends ConfigurationRequest
  final case class StoreUserConfiguration(
                                           userId: UserId,
                                           configuration: UserConfiguration,
                                           replyTo: ActorRef)
    extends ConfigurationRequest

  sealed trait ConfigurationResponse
  final case class ConfigurationFound(
                                       merchantId: MerchantId,
                                       userId: UserId,
                                       merchantConfiguration: MerchantConfiguration,
                                       userConfiguration: UserConfiguration)
    extends ConfigurationResponse
  final case class ConfigurationNotFound(merchanId: MerchantId, userId: UserId) extends ConfigurationResponse
  final case class MerchantConfigurationStored(merchantId: MerchantId) extends ConfigurationResponse
  final case class UserConfigurationStored(userId: UserId) extends ConfigurationResponse
}
