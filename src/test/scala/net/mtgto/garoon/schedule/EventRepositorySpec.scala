package net.mtgto.garoon.schedule

import net.mtgto.garoon.{Authentication, GaroonClient}
import org.apache.axiom.om.{OMFactory, OMElement}
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityIOContext
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import scala.util.Success

class EventRepositorySpec extends Specification with Mockito {
  "EventRepository" should {
    "be able to retrieve an event by its id" in {
      val mockClient = mock[GaroonClient]
      val mockAuth = mock[Authentication]
      val mockOMElement = mock[OMElement]
      val mockFactory = mock[OMFactory]
      mockClient.factory returns mockFactory
      mockFactory.createOMElement(any[String], any) returns mock[OMElement]
      mockClient.sendReceive(any, any, any)(any, any) returns Success(mockOMElement)
      mockOMElement.toString returns
        """<schedule:ScheduleGetEventsByIdResponse xmlns:schedule="http://wsdl.cybozu.co.jp/schedule/2008">
          |<returns>
          |    <schedule_event
          |          id="582682"
          |          event_type="normal"
          |          public_type="public"
          |          detail="イベントタイトル"
          |          description="イベント説明：&#xd;&#xa;鎮守府正面近海の警備に出動せよ！"
          |          version="1383654230"
          |          timezone="Asia/Tokyo"
          |          allday="false"
          |          start_only="false">
          |          <members xmlns="http://schemas.cybozu.co.jp/schedule/2008">
          |            <member>
          |              <user id="1" name="長門" order="0" />
          |            </member>
          |            <member>
          |              <user id="2" name="陸奥" order="1" />
          |            </member>
          |            <member>
          |              <user id="3" name="伊勢" order="2" />
          |            </member>
          |            <member>
          |              <facility id="683" name="鎮守府正面近海" order="2" />
          |            </member>
          |          </members>
          |          <when xmlns="http://schemas.cybozu.co.jp/schedule/2008">
          |            <datetime start="2013-11-14T09:00:00Z" end="2013-11-14T10:15:00Z" />
          |          </when>
          |          <follows xmlns="http://schemas.cybozu.co.jp/schedule/2008">
          |            <follow
          |              id="678934"
          |              text="艦隊のアイドル、那珂ちゃんだよー！&#xd;&#xa;私も連れてってー"
          |              version="1384412275">
          |              <creator user_id="48" name="那珂" date="2013-11-14T06:57:55Z" />
          |            </follow>
          |          </follows>
          |        </schedule_event> </returns>
          |</schedule:ScheduleGetEventsByIdResponse>""".stripMargin
      val repository = new EventRepository(mockClient, mockAuth)
      implicit val context = SyncEntityIOContext
      val eventId = EventId("582682")
      val event = repository.resolve(eventId).get
      event.detail must beSome("イベントタイトル")
    }
  }
}
