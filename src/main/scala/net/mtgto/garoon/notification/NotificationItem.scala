package net.mtgto.garoon.notification

import net.mtgto.garoon.{ItemOperationType, Version}
import scala.xml.Node

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
