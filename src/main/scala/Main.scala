import com.github.nscala_time.time.Imports._
import java.net.URI
import net.mtgto.garoon.user.{UserId, UserRepository}
import net.mtgto.garoon.{Id, GaroonClient}
import net.mtgto.garoon.schedule.{CalendarEventService, EventId, EventRepository}
import org.joda.time.DateTime
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityIOContext

object Main extends App {
  val client = new GaroonClient(args(1), args(2), new URI(args(0)))

  val repository = new EventRepository(client)
  //val repository = new UserRepository(client)
  val service = new CalendarEventService(client)
  implicit val context = SyncEntityIOContext
  val result = repository.resolve(EventId("695369"))
  //val result = repository.findByUserId(Id("1039"), DateTime.now - 1.days to DateTime.now + 1.days)
  //val result = repository.findByUserId(Id("1143"), new DateTime(1386082800000L) to new DateTime(1386169200000L))
  //val result = repository.resolve(UserId("1039"))
  //val result = repository.findByLoginNames(Seq("satoshi_goto"))
  //val result = service.get
  println(result)
}
