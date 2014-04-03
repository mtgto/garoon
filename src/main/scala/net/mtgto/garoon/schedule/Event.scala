package net.mtgto.garoon.schedule

import com.github.nscala_time.time.Imports._
import net.mtgto.garoon.{Id, Version}
import org.sisioh.dddbase.core.model.Entity
import scala.xml.Node

class EventId(override val value: String) extends Id(value)

object EventId {
  def apply(value: String): EventId = new EventId(value)
}

object EventType extends Enumeration {
  /**
   * 通常予定
   */
  val Normal = Value("normal")
  /**
   * 繰り返し予定
   */
  val Repeat = Value("repeat")
  /**
   * 仮予定
   */
  val Temporary = Value("temporary")
  /**
   * 期間予定
   */
  val Banner = Value("banner")
}

object PublicType extends Enumeration {
  /**
   * 公開：全てのユーザーが閲覧可能です。
   */
  val Public = Value("public")

  /**
   * 非公開：参加者のみが閲覧可能です。
   */
  val Private = Value("private")

  /**
   * 公開先設定：参加者と公開先（observers）のみが閲覧可能です。
   */
  val Qualified = Value("qualified")
}

object RepeatEventType extends Enumeration {
  /**
   * 毎日例）10日 ～ 15日までの繰り返し
   */
  val Day = Value("day")

  /**
   * 毎日（土日を除く）例）平日のみの繰り返し
   */
  val Weekday = Value("weekday")

  /**
   * 毎週例）毎週月曜日
   */
  val Week = Value("week")

  /**
   * 毎月第一週例）毎月第一週の月曜日の繰り返し
   */
  val Week1st = Value("1stweek")

  /**
   * 毎月第二週例）毎月第二週の火曜日の繰り返し
   */
  val Week2nd = Value("2ndweek")

  /**
   * 毎月第三週例）毎月第三週の水曜日の繰り返し
   */
  val Week3rd = Value("3rdweek")

  /**
   * 毎月第四週例）毎月第四週の木曜日の繰り返し
   */
  val Week4th = Value("4thweek")

  /**
   * 毎月最終週例）毎月最終週の金曜日の繰り返し
   */
  val Week5th = Value("lastweek")

  /**
   * 毎月例）毎月1日の繰り返し
   */
  val Month = Value("month")
}

/**
 * https://developers.cybozu.com/ja/garoon-api/schedule-data-format.html#EventType
 */
trait Event extends Entity[EventId] {
  override val identity: EventId
  val eventType: EventType.Value
  val version: Version
  val publicType: Option[PublicType.Value]
  /**
   * 予定メニュー
   */
  val plan: Option[String]

  /**
   * タイトル
   */
  val detail: Option[String]

  /**
   * メモ
   */
  val description: Option[String]

  /**
   * 終日予定否か
   */
  val allday: Option[Boolean]

  /**
   * 開始時刻のみ設定されているか否か
   */
  val startOnly: Option[Boolean]

  /**
   * 他のユーザーの予定が非公開で閲覧できない場合にtrue
   */
  val hiddenPrivate: Option[Boolean]

  /**
   * 参加者一覧
   */
  val members: Seq[User]

  /**
   * 施設
   */
  val facilities: Seq[Facility]

  /**
   * 繰り返し予定の情報
   */
  val repeatInfo: Option[RepeatInfo]

  /**
   * 時間情報
   */
  val when: Option[EventDateTime]
}

object Event {
  private case class DefaultEvent(identity: EventId, eventType: EventType.Value, version: Version,
                                   publicType: Option[PublicType.Value], plan: Option[String],
                                   detail: Option[String], description: Option[String],
                                   allday: Option[Boolean], startOnly: Option[Boolean],
                                   hiddenPrivate: Option[Boolean], members: Seq[User],
                                   facilities: Seq[Facility], repeatInfo: Option[RepeatInfo],
                                   when: Option[EventDateTime]) extends Event

  def apply(node: Node): Event = {
    val identity = EventId((node \ "@id").text)
    val eventType = EventType.withName((node \ "@event_type").text)
    val version = Version((node \ "@version").text)
    val publicType = (node \ "@public_type").headOption.map(s => PublicType.withName(s.text))
    val plan = (node \ "@plan").headOption.map(_.text)
    val detail = (node \ "@detail").headOption.map(_.text)
    val description = (node \ "@description").headOption.map(_.text)
    // timezone,end_timezoneスキップ
    val allday = (node \ "@allday").headOption.map(_.text.toBoolean)
    val startOnly = (node \ "@start_only").headOption.map(_.text.toBoolean)
    val hiddenPrivate = (node \ "@hidden_private").headOption.map(_.text.toBoolean)
    val members = (node \ "members" \ "member" \ "user") map { user =>
      val userId = Id((user \ "@id").text)
      val userName = (user \ "@name").text
      val userOrder = (user \ "@order").headOption.map(o => Id(o.text))
      User(userId, userName, userOrder)
    }
    val facilities = (node \ "members" \ "member" \ "facility") map { facility =>
      val facilityId = Id((facility \ "@id").text)
      val facilityName = (facility \ "@name").text
      val facilityOrder = (facility \ "@order").headOption.map(o => Id(o.text))
      Facility(facilityId, facilityName, facilityOrder)
    }
    val repeatInfo = (node \ "repeat_info").headOption map { repeatInfo =>
      val repeatType = RepeatEventType.withName((repeatInfo \ "condition" \ "@type").text)
      val startDate = new LocalDate((repeatInfo \ "condition" \ "@start_date").text)
      val endDate = (repeatInfo \ "condition" \ "@end_date").headOption.map(d => new LocalDate(d.text))
      val startTime = (repeatInfo \ "condition" \ "@start_time").headOption.map(d => new LocalTime(d.text))
      val endTime = (repeatInfo \ "condition" \ "@end_time").headOption.map(d => new LocalTime(d.text))
      val day = (repeatInfo \ "condition" \ "@day").headOption.map(_.text.toInt)
      val week = (repeatInfo \ "condition" \ "@week").headOption.map(_.text.toInt)
      val exclusiveDatetimes = (repeatInfo \ "exclusive_datetimes").headOption.map {
        _ \ "exclusive_datetime" map { exclusiveDatetime =>
          val start = new DateTime((exclusiveDatetime \ "@start").text)
          val end = new DateTime((exclusiveDatetime \ "@end").text)
          new Interval(start, end)
        }
      }
      RepeatInfo(repeatType, startDate, endDate, startTime, endTime, day, week, exclusiveDatetimes)
    }
    val when = (node \ "when" \ "datetime").headOption.map { datetime =>
      val start = new DateTime((datetime \ "@start").text)
      val end = (datetime \ "@end").headOption.map(e => new DateTime(e.text))
      val facilityCode = (datetime \ "@facility_code").headOption.map(e => Id(e.text))
      EventDateTime(start, end, facilityCode)
    } orElse {
      (node \ "when" \ "date").headOption.map { date =>
        val start = parseDateTimeForInvalidString((date \ "@start").text)
        val end = (date \ "@end").headOption.map(e => parseEndDateTimeForInvalidString(e.text))
        val facilityCode = (date \ "@facility_code").headOption.map(e => Id(e.text))
        EventDateTime(start, end, facilityCode)
      }
    }

    DefaultEvent(identity, eventType, version, publicType, plan, detail, description, allday, startOnly,
      hiddenPrivate, members, facilities, repeatInfo, when)
  }

  val wrongDateTimeRegex = """(\d{4}-\d{2}-\d{2}) (\d{2}:\d{2})""".r

  private[this] def parseDateTimeForInvalidString(text: String): DateTime = {
    wrongDateTimeRegex findFirstIn text match {
      case Some(wrongDateTimeRegex(date, time)) => DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(text)
      case _ => new DateTime(text)
    }
  }

  private[this] def parseEndDateTimeForInvalidString(text: String): DateTime = {
    wrongDateTimeRegex findFirstIn text match {
      case Some(wrongDateTimeRegex(date, time)) => DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime(text)
      case _ => new DateTime(text) + 1.day - 1.second
    }
  }
}