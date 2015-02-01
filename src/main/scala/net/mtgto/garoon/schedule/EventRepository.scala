package net.mtgto.garoon.schedule

import com.github.nscala_time.time.Imports._
import net.mtgto.garoon.{Authentication, Id, GaroonClient}
import org.sisioh.dddbase.core.lifecycle.{EntityNotFoundException, EntityIOContext}
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityReader
import scala.util.Try
import scala.xml.XML

class EventRepository(client: GaroonClient, val auth: Authentication) extends SyncEntityReader[EventId, Event] {
  def resolve(identity: EventId)(implicit ctx: EntityIOContext[Try]): Try[Event] = {
    val actionName = "ScheduleGetEventsById"
    val parameters = client.factory.createOMElement("parameters", null)
    val eventNode = client.factory.createOMElement("event_id", null)
    eventNode.setText(identity.value)
    parameters.addChild(eventNode)

    val result = client.sendReceive(actionName, "/cbpapi/schedule/api", parameters)(auth, None)
    result.map { element =>
      val node = XML.loadString(element.toString)
      (node \ "returns" \ "schedule_event").map(Event(_)).headOption.getOrElse(throw new EntityNotFoundException)
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

    val result = client.sendReceive(actionName, "/cbpapi/schedule/api", parameters)(auth, None)
    result.map { element =>
      val node = XML.loadString(element.toString)
      node \ "returns" \ "schedule_event" map (Event(_))
    }
  }
}
