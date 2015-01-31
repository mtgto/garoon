package net.mtgto.garoon.notification

import java.util.NoSuchElementException
import net.mtgto.garoon.{ItemOperationType, Version, Id}
import org.sisioh.dddbase.core.model.Entity
import scala.xml.Node

sealed class ModuleId(override val value: String) extends Id(value)

object ModuleId {
  val ScheduleId = new ModuleId("grn.schedule")

  val MessageId = new ModuleId("grn.message")

  val BulletinId = new ModuleId("grn.bulletin")

  val PhoneMessageId = new ModuleId("grn.phonemessage")

  val MailId = new ModuleId("grn.workflow")

  val ReportId = new ModuleId("grn.report")

  def apply(value: String): ModuleId = {
    value match {
      case ScheduleId.value => ScheduleId
      case MessageId.value => MessageId
      case BulletinId.value => BulletinId
      case PhoneMessageId.value => PhoneMessageId
      case MailId.value => MailId
      case ReportId.value => ReportId
      case _ if value.startsWith("grn.space.") => new ModuleId(value)
      case _ => throw new NoSuchElementException(s"${value} is invalid type for module.")
    }
  }
}

/**
 * 通知の更新情報
 * @see https://cybozudev.zendesk.com/hc/ja/articles/202495980#step5
 */
case class NotificationItem(
  version: Version,
  operationType: ItemOperationType.Value,
  moduleId: ModuleId,
  item: String
)

object NotificationItem {
  def apply(node: Node): NotificationItem = {
    val version = Version((node \ "@version").text)
    val operationType = ItemOperationType.withName((node \ "@operation").text)
    val moduleId = ModuleId((node \ "notification_id" \ "@module_id").text)
    val item = (node \ "notification_id" \ "@item").text
    NotificationItem(version, operationType, moduleId, item)
  }
}
