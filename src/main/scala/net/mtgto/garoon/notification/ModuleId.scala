package net.mtgto.garoon.notification

import net.mtgto.garoon.Id

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
