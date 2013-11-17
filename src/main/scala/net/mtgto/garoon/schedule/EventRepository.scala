package net.mtgto.garoon.schedule

import com.cybozu.garoon3.common.CBServiceClient
import com.cybozu.garoon3.schedule.ScheduleGetEventsById
import org.sisioh.dddbase.core.lifecycle.EntityIOContext
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityReader
import scala.util.Try
import scala.xml.XML

class EventRepository(client: CBServiceClient) extends SyncEntityReader[EventId, Event] {
  def resolve(identity: EventId)(implicit ctx: EntityIOContext[Try]): Try[Event] = {
    val action = new ScheduleGetEventsById()
    action.addID(identity.value.toInt)
    val result = client.sendReceive(action)
    //result.getTextAsStream(true)
    val node = XML.loadString(result.toString)
    Try(Event((node \ "returns" \ "schedule_event").head))
  }

  def containsByIdentity(identity: EventId)(implicit ctx: EntityIOContext[Try]): Try[Boolean] =
    resolve(identity).map(_ => true)
}
