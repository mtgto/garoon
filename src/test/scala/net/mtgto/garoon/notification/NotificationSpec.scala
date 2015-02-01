package net.mtgto.garoon.notification

import java.net.URI
import com.github.nscala_time.time.Imports._
import net.mtgto.garoon.Id
import org.specs2.mutable.Specification

class NotificationSpec extends Specification {
  "NotificationItemSpec" should {
    "be able to create a notification from valid xml" in {
      val xml =
          <notification
          module_id="grn.schedule"
          item="972752"
          status="create"
          is_history="false"
          version="1422689524"
          receive_datetime="2015-01-31T07:32:04Z"
          subject="01/07（水） アニメ艦これ放送開始"
          subject_url="http://garoon.example.com/cbgrn/grn.cgi/schedule/view?uid=1&amp;event=972752&amp;bdate=2015-02-06&amp;nid=1877145"
          abstract="登録"
          abstract_url="http://garoon.example.com/cbgrn/grn.cgi/schedule/view?uid=1&amp;event=972752&amp;bdate=2015-02-06&amp;nid=1877145"
          sender_name="「艦これ」連合艦隊司令部"
          sender_id="1000"
          sender_url="http://garoon.example.com/cbgrn/grn.cgi/grn/user_view?uid=1000"
          attached="false"/>
      val notification = Notification(xml)
      notification.moduleId must_== ModuleId.ScheduleId
      notification.item must_== "972752"
      notification.status must_== StatusType.Create
      notification.isChecked must beFalse
      notification.readDate must beNone
      notification.receiveDate must beSome(new DateTime("2015-01-31T07:32:04Z"))
      notification.subject must beSome("01/07（水） アニメ艦これ放送開始")
      notification.subjectUrl must beSome(new URI("http://garoon.example.com/cbgrn/grn.cgi/schedule/view?uid=1&event=972752&bdate=2015-02-06&nid=1877145"))
      notification.subjectIcon must beNone
      notification.content must beSome("登録")
      notification.contentUrl must beSome(new URI("http://garoon.example.com/cbgrn/grn.cgi/schedule/view?uid=1&event=972752&bdate=2015-02-06&nid=1877145"))
      notification.senderName must beSome("「艦これ」連合艦隊司令部")
      notification.senderId must beSome(Id("1000"))
      notification.senderUrl must beSome(new URI("http://garoon.example.com/cbgrn/grn.cgi/grn/user_view?uid=1000"))
      notification.isAttached must beSome(false)
    }
  }
}
