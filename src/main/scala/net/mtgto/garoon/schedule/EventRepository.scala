package net.mtgto.garoon.schedule

import org.sisioh.dddbase.core.lifecycle.EntityIOContext
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityReader
import scala.util.Try
import scala.xml.XML
import net.mtgto.garoon.GaroonClient

class EventRepository(client: GaroonClient) extends SyncEntityReader[EventId, Event] {
  def resolve(identity: EventId)(implicit ctx: EntityIOContext[Try]): Try[Event] = {
    val actionName = "ScheduleGetEventsById"
    val parameters = client.factory.createOMElement("parameters", null)
    val eventNode = client.factory.createOMElement("event_id", null)
    eventNode.setText(identity.value)
    parameters.addChild(eventNode)

    val result = client.sendReceive(actionName, "/cbpapi/schedule/api", parameters)
    result.map { element =>
      val node = XML.loadString(element.toString)
      Event((node \ "returns" \ "schedule_event").head)
    }
  }

  def containsByIdentity(identity: EventId)(implicit ctx: EntityIOContext[Try]): Try[Boolean] =
    resolve(identity).map(_ => true)
}
