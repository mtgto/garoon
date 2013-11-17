import java.net.URI
import net.mtgto.garoon.GaroonClient
import net.mtgto.garoon.schedule.{EventId, EventRepository}
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityIOContext

object Main extends App {
  val client = new GaroonClient(args(1), args(2), new URI(args(0)))

  val repository = new EventRepository(client)
  implicit val context = SyncEntityIOContext
  val result = repository.resolve(EventId("622594"))
  println(result.toString)
}
