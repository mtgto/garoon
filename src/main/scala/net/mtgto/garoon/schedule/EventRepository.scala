package net.mtgto.garoon.schedule

import com.github.nscala_time.time.Imports._
import org.sisioh.dddbase.core.lifecycle.EntityIOContext
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityReader
import scala.util.Try
import scala.xml.XML
import net.mtgto.garoon.{Id, GaroonClient}

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

  def findByUserId(userId: Id, interval: Interval): Try[Seq[Event]] = {
    val actionName = "ScheduleGetEventsByTarget"
    val parameters = client.factory.createOMElement("parameters", null)
    parameters.addAttribute("start", interval.getStart.toString(), null)
    parameters.addAttribute("end", interval.getEnd.toString(), null)

    val memberNode = client.factory.createOMElement("user", null)
    memberNode.addAttribute("id", userId.value, null)
    parameters.addChild(memberNode)

    val result = client.sendReceive(actionName, "/cbpapi/schedule/api", parameters)
    result.map { element =>
      val node = XML.loadString(element.toString)
      node \ "returns" \ "schedule_event" map (Event(_))
    }
  }
}
