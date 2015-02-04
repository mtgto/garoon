package net.mtgto.garoon.user

import net.mtgto.garoon.{Authentication, GaroonClient}
import org.sisioh.dddbase.core.lifecycle.{EntityIOContext, EntityNotFoundException}
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityReader
import scala.util.Try
import scala.xml.XML

class UserRepository(client: GaroonClient, auth: Authentication) extends SyncEntityReader[UserId, User] {
  def resolve(identity: UserId)(implicit ctx: EntityIOContext[Try]): Try[User] = {
    val actionName = "BaseGetUsersById"
    val parameters = client.factory.createOMElement("parameters", null)
    val eventNode = client.factory.createOMElement("user_id", null)
    eventNode.setText(identity.value)
    parameters.addChild(eventNode)

    val result = client.sendReceive(actionName, "/cbpapi/base/api", parameters)(auth, None)
    result.map { element =>
      val node = XML.loadString(element.toString)
      (node \ "returns" \ "user").map(User(_)).headOption.getOrElse(throw new EntityNotFoundException)
    }
  }

  def containsByIdentity(identity: UserId)(implicit ctx: EntityIOContext[Try]): Try[Boolean] =
    resolve(identity).map(_ => true)

  def findByLoginNames(loginNames: Seq[String]): Try[Seq[User]] = {
    val actionName = "BaseGetUsersByLoginName"
    val parameters = client.factory.createOMElement("parameters", null)
    loginNames.foreach { loginName =>
      val loginNameNode = client.factory.createOMElement("login_name", null)
      loginNameNode.setText(loginName)
      parameters.addChild(loginNameNode)
    }

    val result = client.sendReceive(actionName, "/cbpapi/base/api", parameters)(auth, None)
    result.map { element =>
      val node = XML.loadString(element.toString)
      (node \ "returns" \ "user").map(User(_))
    }
  }

  def getLoggedInUserId: Try[UserId] = {
    val actionName = "UtilGetLoginUserId"
    val parameters = client.factory.createOMElement("parameters", null)
    val result = client.sendReceive(actionName, "/util_api/util/api", parameters)(auth, None)
    result.map { element =>
      val node = XML.loadString(element.toString)
      UserId((node \ "returns" \ "user_id").text)
    }
  }
}
