import com.github.nscala_time.time.Imports._
import java.net.URI
import net.mtgto.garoon.notification.NotificationService
import net.mtgto.garoon.user.{UserId, UserRepository}
import net.mtgto.garoon._
import net.mtgto.garoon.schedule.{CalendarEventService, EventId, EventRepository}
import org.joda.time.DateTime
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityIOContext
import scala.util.{Success, Failure}

object Main extends App {
  val client = new GaroonClient(new URI(args(0)))

  //val repository = new EventRepository(client)
  //val repository = new UserRepository(client)
  //val service = new NotificationService(client)
  val loginService = new LoginService(client)
  implicit val context = SyncEntityIOContext
  //val result = repository.resolve(EventId("695369"))
  //val result = repository.findByUserId(Id("1039"), DateTime.now - 1.days to DateTime.now + 1.days)
  //val result = repository.findByUserId(Id("1143"), new DateTime(1386082800000L) to new DateTime(1386169200000L))
  //val result = repository.resolve(UserId("1039"))
  //val result = repository.findByLoginNames(Seq("satoshi_goto"))
  //val result = service.get
  //val result = service.getNotificationVersions(DateTime.now - 3.days)
    //.map(list => service.getNotifications(list.map(item => (item.moduleId, item.item))))
  //val result = service.login(args(1), args(2))

  // セッションを発行してからの実行
  //  loginService.login(args(1), args(2)).foreach { cookie =>
  //    println("BBBBB"+cookie)
  //    loginService.getRequestToken(cookie) match {
  //      case Failure(e) => e.printStackTrace()
  //      case Success(requestToken) =>
  //        val service2 = new CalendarEventService(client, SessionCookie(cookie))
  //        val result = service2.get
  //        println(result)
  //    }
  // パスワードによる実行
  //  val service = new CalendarEventService(client, Password(args(1), args(2)))
  //  val result = service.get
  //  println(result)
}
