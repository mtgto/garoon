package net.mtgto.garoon.schedule

import org.joda.time.{Interval, LocalTime, LocalDate}

case class RepeatInfo(
                       repeatEventType: RepeatEventType.Value,
                       startDate: LocalDate,
                       endDate: Option[LocalDate],
                       startTime: Option[LocalTime],
                       endTime: Option[LocalTime],
                       day: Option[Int],
                       week: Option[Int],
                       exclusiveDatetimes: Option[Seq[Interval]])

