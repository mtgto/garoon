import com.cybozu.garoon3.common.{CBServiceClient, Config}
import net.mtgto.garoon.schedule.{EventId, EventRepository}
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityIOContext

object Main extends App {
  val client = new CBServiceClient
  val config = new Config("login.ini")
  client.load(config)

  val repository = new EventRepository(client)
  implicit val context = SyncEntityIOContext
  val result = repository.resolve(EventId("622594"))
  println(result.toString)
}
