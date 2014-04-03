package net.mtgto.garoon.schedule

import org.joda.time.LocalDate
import scala.xml.Node

object CalendarEventType extends Enumeration {
  /**
   * イベントタイプが「祝日」の予定。システム管理画面からのみ登録可能。
   */
  val PublicHoliday = Value("public_holiday")

  /**
   * イベントタイプが「記念日」の予定。個人設定からのみ登録可能。
   */
  val MemorialHoliday = Value("memorial_holiday")

  /**
   * システム管理画面から登録されたメモを表す。
   */
  val SystemMemo = Value("system_memo")

  /**
   * 個人設定から登録されたメモを表す。
   */
  val UserMemo = Value("user_memo")

  /**
   * システムから登録した平日
   */
  val PublicWorkday = Value("public_workday")
}

case class CalendarEvent(
  date: LocalDate,
  content: String,
  eventType: CalendarEventType.Value
)

object CalendarEvent {
  def apply(node: Node): CalendarEvent = {
    val date = new LocalDate((node \ "@date").text)
    val content = (node \ "@content").text
    val eventType = CalendarEventType.withName((node \ "@type").text)
    CalendarEvent(date, content, eventType)
  }
}
