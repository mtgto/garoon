package net.mtgto.garoon.notification

import java.net.URI
import com.github.nscala_time.time.Imports._
import net.mtgto.garoon.{Version, Id}
import scala.xml.Node

trait Notification {
  val moduleId: ModuleId
  val item: String
  val status: StatusType.Value
  /// 確認済みかどうか
  val isChecked: Boolean
  val readDate: Option[DateTime]
  val receiveDate: Option[DateTime]
  val subject: Option[String]
  val subjectUrl: Option[URI]
  val subjectIcon: Option[String]
  val senderName: Option[String]
  /// 通知の内容
  val content: Option[String]
  val contentUrl: Option[URI]
  val contentIcon: Option[String]
  val senderId: Option[Id]
  val senderUrl: Option[URI]
  val isAttached: Option[Boolean]
  val version: Option[Version]
}

object Notification {
  private[this] case class DefaultNotification(
    moduleId: ModuleId,
    item: String,
    status: StatusType.Value,
    isChecked: Boolean,
    readDate: Option[DateTime],
    receiveDate: Option[DateTime],
    subject: Option[String],
    subjectUrl: Option[URI],
    subjectIcon: Option[String],
    content: Option[String],
    contentUrl: Option[URI],
    contentIcon: Option[String],
    senderName: Option[String],
    senderId: Option[Id],
    senderUrl: Option[URI],
    isAttached: Option[Boolean],
    version: Option[Version]
  ) extends Notification
  def apply(node: Node): Notification = {
    val moduleId = ModuleId((node \ "@module_id").text)
    val item = (node \ "@item").text
    val status = StatusType.withName((node \ "@status").text)
    val isChecked = (node \ "@is_history").text.toBoolean
    val readDate = (node \ "@read_datetime").headOption.map(d => new DateTime(d.text))
    val receiveDate = (node \ "@receive_datetime").headOption.map(d => new DateTime(d.text))
    val subject = (node \ "@subject").headOption.map(_.text)
    val subjectUrl = (node \ "@subject_url").headOption.map(d => new URI(d.text))
    val subjectIcon = (node \ "@subject_icon").headOption.map(_.text)
    val content = (node \ "@abstract").headOption.map(_.text)
    val contentUrl = (node \ "@abstract_url").headOption.map(d => new URI(d.text))
    val contentIcon = (node \ "@abstract_icon").headOption.map(_.text)
    val senderName = (node \ "@sender_name").headOption.map(_.text)
    val senderId = (node \ "@sender_id").headOption.map(d => Id(d.text))
    val senderUrl = (node \ "@sender_url").headOption.map(d => new URI(d.text))
    val isAttached = (node \ "@attached").headOption.map(_.text.toBoolean)
    val version = (node \ "@item").headOption.map(n => Version(n.text))
    DefaultNotification(
      moduleId, item, status, isChecked, readDate, receiveDate, subject, subjectUrl, subjectIcon,
      content, contentUrl, contentIcon, senderName, senderId, senderUrl, isAttached, version
    )
  }
}
