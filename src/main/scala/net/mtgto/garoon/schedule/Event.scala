package net.mtgto.garoon.schedule

import org.sisioh.dddbase.core.model.Entity
import net.mtgto.garoon.{Id, Version}
import scala.xml.Node
import java.util.TimeZone

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

/**
 * https://developers.cybozu.com/ja/garoon-api/schedule-data-format.html#EventType
 *
 * @param identity
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
}

object Event {
  private case class DefaultEvent(identity: EventId, eventType: EventType.Value, version: Version,
                                   publicType: Option[PublicType.Value], plan: Option[String],
                                   detail: Option[String], description: Option[String],
                                   allday: Option[Boolean], startOnly: Option[Boolean],
                                   hiddenPrivate: Option[Boolean], members: Seq[User],
                                   facilities: Seq[Facility]) extends Event

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

    DefaultEvent(identity, eventType, version, publicType, plan, detail, description, allday, startOnly,
      hiddenPrivate, members, facilities)
  }
}