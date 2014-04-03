package net.mtgto.garoon.schedule

import org.joda.time.DateTime
import net.mtgto.garoon.Id

case class EventDateTime(
  start: DateTime,
  end: Option[DateTime],
  facilityCode: Option[Id]
)
