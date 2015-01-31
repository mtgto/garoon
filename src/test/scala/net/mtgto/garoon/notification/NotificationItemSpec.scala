package net.mtgto.garoon.notification

import net.mtgto.garoon.{Version, ItemOperationType}
import org.specs2.mutable.Specification

class NotificationItemSpec extends Specification {
  "NotificationItemSpec" should {
    "be able to create an item from valid xml" in {
      val xml =
        <notification_item version="1422417867" operation="add">
          <notification_id module_id="grn.schedule" item="969505"/>
        </notification_item>
      val item = NotificationItem(xml)
      item.version must_== Version("1422417867")
      item.operationType must_== ItemOperationType.Add
      item.moduleId must_== ModuleId.ScheduleId
      item.item must_== "969505"
    }
  }
}
