import com.github.nscala_time.time.Imports._
import java.net.URI
import net.mtgto.garoon.{Id, GaroonClient}
import net.mtgto.garoon.schedule.{EventId, EventRepository}
import org.joda.time.DateTime
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityIOContext

object Main extends App {
  val client = new GaroonClient(args(1), args(2), new URI(args(0)))

  val repository = new EventRepository(client)
  implicit val context = SyncEntityIOContext
  //val result = repository.resolve(EventId("622594"))
  val result = repository.findByUserId(Id("1031"), DateTime.now to DateTime.now + 2.days)
  println(result)
}
