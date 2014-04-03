package net.mtgto.garoon.schedule

import net.mtgto.garoon.GaroonClient
import scala.util.Try
import scala.xml.XML

class CalendarEventService(client: GaroonClient) {
  def get: Try[Seq[CalendarEvent]] = {
    val actionName = "BaseGetCalendarEvents"
    val parameters = client.factory.createOMElement("parameters", null)

    val result = client.sendReceive(actionName, "/cbpapi/base/api", parameters)
    result.map { element =>
      val node = XML.loadString(element.toString)
      (node \ "returns" \ "calendar_event").map(CalendarEvent(_))
    }
  }
}
