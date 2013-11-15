package net.mtgto.garoon.schedule

import org.specs2.mutable.Specification
import net.mtgto.garoon.{Version, Id}

class EventSpec extends Specification {
  "Event" should {
    "be able to create an event from valid xml" in {
      val xml =
        <schedule_event
          id="582682"
          event_type="normal"
          public_type="public"
          detail="イベントタイトル"
          description="イベント説明：&#xd;&#xa;鎮守府正面近海の警備に出動せよ！"
          version="1383654230"
          timezone="Asia/Tokyo"
          allday="false"
          start_only="false">
          <members xmlns="http://schemas.cybozu.co.jp/schedule/2008">
            <member>
              <user id="1" name="長門" order="0" />
            </member>
            <member>
              <user id="2" name="陸奥" order="1" />
            </member>
            <member>
              <user id="3" name="伊勢" order="2" />
            </member>
            <member>
              <facility id="683" name="鎮守府正面近海" order="2" />
            </member>
          </members>
          <when xmlns="http://schemas.cybozu.co.jp/schedule/2008">
            <datetime start="2013-11-14T09:00:00Z" end="2013-11-14T10:15:00Z" />
          </when>
          <follows xmlns="http://schemas.cybozu.co.jp/schedule/2008">
            <follow
              id="678934"
              text="艦隊のアイドル、那珂ちゃんだよー！&#xd;&#xa;私も連れてってー"
              version="1384412275">
              <creator user_id="48" name="那珂" date="2013-11-14T06:57:55Z" />
            </follow>
          </follows>
        </schedule_event>
      val event = Event(xml)
      event.identity must_== EventId("582682")
      event.eventType must_== EventType.Normal
      event.publicType must beSome(PublicType.Public)
      event.detail must beSome("イベントタイトル")
      event.description must beSome("イベント説明：\r\n鎮守府正面近海の警備に出動せよ！")
      event.version must_== Version("1383654230")
      event.allday must beSome(false)
      event.startOnly must beSome(false)
      event.members must_== Seq(
        User(Id("1"), "長門", Some(Id("0"))),
        User(Id("2"), "陸奥", Some(Id("1"))),
        User(Id("3"), "伊勢", Some(Id("2"))))
      event.facilities must_== Seq(
        Facility(Id("683"), "鎮守府正面近海", Some(Id("2")))
      )
    }
  }
}
