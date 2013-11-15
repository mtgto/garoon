import com.cybozu.garoon3.common.{CBServiceClient, Config}
import com.cybozu.garoon3.schedule.{MemberType, ScheduleGetEventsByTarget}
import java.util.Date

object Main extends App {
  val client = new CBServiceClient
  val config = new Config("login.ini")
  client.load(config)

  val action = new ScheduleGetEventsByTarget
  action.setMember(MemberType.USER, 1031)
  action.setStart(new Date(113, 10, 14))
  action.setEnd(new Date(113, 10, 15))

  val result = client.sendReceive(action)
  println(result)
}
