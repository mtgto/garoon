package net.mtgto.garoon.notification

import com.github.nscala_time.time.Imports._
import net.mtgto.garoon.GaroonClient

import scala.util.Try
import scala.xml.XML

class NotificationService(client: GaroonClient) {
  def getNotificationVersions(startTime: DateTime): Try[Seq[NotificationItem]] = {
    val actionName = "NotificationGetNotificationVersions"
    val parameters = client.factory.createOMElement("parameters", null)
    parameters.addAttribute("start", startTime.toString(), null)
    //parameters.addAttribute("end", interval.getEnd.toString(), null)

    val result = client.sendReceive(actionName, "/cbpapi/notification/api", parameters)
    result.map { element =>
      val node = XML.loadString(element.toString)
      (node \ "returns" \ "notification_item").map(NotificationItem(_))
    }
  }

  def getNotifications(notificationIds: Seq[Tuple2[ModuleId, String]]): Try[Seq[Notification]] = {
    val actionName = "NotificationGetNotificationsById"
    val parameters = client.factory.createOMElement("parameters", null)
    notificationIds.foreach {
      case (moduleId: ModuleId, item: String) =>
        val notificationNode = client.factory.createOMElement("notification_id", null)
        notificationNode.addAttribute("module_id", moduleId.value, null)
        notificationNode.addAttribute("item", item, null)
        parameters.addChild(notificationNode)
    }

    val result = client.sendReceive(actionName, "/cbpapi/notification/api", parameters)
    result.map { element =>
      val node = XML.loadString(element.toString)
      (node \ "returns" \ "notification").map(Notification(_))
    }
  }
}
