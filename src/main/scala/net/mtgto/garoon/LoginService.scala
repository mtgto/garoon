package net.mtgto.garoon

import scala.util.Try
import scala.xml.XML

class LoginService(client: GaroonClient) {
  def login(username: String, password: String): Try[Cookie] = {
    val actionName = "UtilLogin"
    val parameters = client.factory.createOMElement("parameters", null)
    val loginNode = client.factory.createOMElement("login_name", null)
    loginNode.setText(username)
    parameters.addChild(loginNode)
    val passwordNode = client.factory.createOMElement("password", null)
    passwordNode.setText(password)
    parameters.addChild(passwordNode)

    val result = client.sendReceive(actionName, "/util_api/util/api", parameters)(NoAuth, None)
    result.map { element =>
      val node = XML.loadString(element.toString)
      LoginService.cookiePattern.findFirstMatchIn((node \ "returns" \ "cookie").text) match {
        case Some(m) => Cookie(m.group(1))
        case _ => throw new RuntimeException("failed to login")
      }
    }
  }

  def getRequestToken(cookie: Cookie): Try[RequestToken] = {
    val actionName = "UtilGetRequestToken"
    val parameters = client.factory.createOMElement("parameters", null)
    val result = client.sendReceive(actionName, "/util_api/util/api", parameters)(SessionCookie(cookie), None)
    result.map { element =>
      val node = XML.loadString(element.toString)
      RequestToken((node \ "returns" \ "request_token").text)
    }
  }
}

object LoginService {
  private val cookiePattern = """CBSESSID=(\w+)""".r
}
