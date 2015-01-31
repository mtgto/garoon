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
      println(element.toString)
      val node = XML.loadString(element.toString)
      (node \ "returns" \ "notification_item").map(NotificationItem(_))
    }
  }
}
