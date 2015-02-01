package net.mtgto.garoon

import com.github.nscala_time.time.Imports._
import java.net.URI
import org.apache.axis2.Constants
import org.apache.axis2.addressing.EndpointReference
import org.apache.axis2.client.{Options, ServiceClient}
import org.apache.axiom.om.{OMAbstractFactory, OMElement}
import org.apache.axiom.soap.SOAP12Constants
import org.apache.axis2.transport.http.HTTPConstants
import org.apache.commons.httpclient.Header
import scala.util.Try

/**
 * ガルーンSOAP APIクライアント
 * @note このクラスはスレッドセーフではありません.
 *
 * @param uri
 */
class GaroonClient(uri: URI) {
  val factory = OMAbstractFactory.getOMFactory

  private[this] val serviceClient = new ServiceClient

  private[this] val scheme = uri.getScheme

  private[this] val namespace = factory.createOMNamespace("http://wsdl.cybozu.co.jp/api/2008", "tns")

  def sendReceive(actionName: String, actionPath: String, parameters: OMElement)
      (implicit auth: Authentication, requestTokenOpt: Option[RequestToken] = None): Try[OMElement] = {
    serviceClient.removeHeaders

    val header = createHeader(actionName, DateTime.now, DateTime.now + 1.day, auth)
    serviceClient.addHeader(header)

    val option = createOption(actionName, actionPath, auth)
    serviceClient.setOptions(option)

    requestTokenOpt.foreach { requestToken =>
        val tokenNode = factory.createOMElement("request_token", null)
        tokenNode.setText(requestToken.value)
        parameters.addChild(tokenNode)
    }
    val request = factory.createOMElement(actionName, namespace)
    request.addChild(parameters)

    Try(serviceClient.sendReceive(request))
  }

  private[this] def createHeader(actionName: String, createdTime: DateTime, expiredTime: DateTime, auth: Authentication): OMElement = {
    val namespace = factory.createOMNamespace(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI, "soapenv")
    val header = factory.createOMElement("Header", namespace)

    header.addChild(createAction(actionName))
    val security = auth match {
      case Password(username, password) => createSecurity(username, password)
      case _ => createSecurity("", "")
    }
    header.addChild(security)
    header.addChild(createTimestamp(createdTime, expiredTime))

    header
  }

  private[this] def createAction(actionName: String): OMElement = {
    val namespace = factory.createOMNamespace("http://schemas.xmlsoap.org/ws/2003/03/addressing", "")
    val actionElement = factory.createOMElement("Action", namespace)
    actionElement.addChild(factory.createOMText(actionElement, actionName))
    actionElement
  }

  private[this] def createSecurity(username: String, password: String): OMElement = {
    val namespace = factory.createOMNamespace("http://schemas.xmlsoap.org/ws/2002/12/secext", "")
    val securityElement = factory.createOMElement("Security", namespace)
    val usernameTokenElement = factory.createOMElement("UsernameToken", namespace)
    val usernameElement = factory.createOMElement("Username", namespace)
    usernameElement.addChild(factory.createOMText(usernameElement, username))
    val passwordElement = factory.createOMElement("Password", namespace)
    passwordElement.addChild(factory.createOMText(passwordElement, password))

    usernameTokenElement.addChild(usernameElement)
    usernameTokenElement.addChild(passwordElement)
    securityElement.addChild(usernameTokenElement)

    securityElement
  }

  private[this] def createTimestamp(createdTime: DateTime, expiredTime: DateTime): OMElement = {
    val namespace = factory.createOMNamespace("http://schemas.xmlsoap.org/ws/2002/07/utility", "")
    val timestampElement = factory.createOMElement("Timestamp", namespace)

    val created = factory.createOMElement("Created", namespace)
    created.addChild(factory.createOMText(created, createdTime.toString()))
    val expired = factory.createOMElement("Expires", namespace)
    expired.addChild(factory.createOMText(created, expiredTime.toString()))

    timestampElement.addChild(created)
    timestampElement.addChild(expired)
    timestampElement
  }

  private[this] def createOption(actionName: String, actionPath: String, auth: Authentication): Options = {
    val options = new Options
    val actionUri = uri.toString + actionPath
    options.setTo(new EndpointReference(actionUri.toString))
    options.setTransportInProtocol(scheme)
    options.setProperty(HTTPConstants.CHUNKED, Constants.VALUE_FALSE)
    options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)
    options.setAction(actionName)
    auth match {
      case SessionCookie(cookie) => setCookieToOptions(options, cookie)
      case _ => ()
    }
    options
  }

  private[this] def setCookieToOptions(options: Options, cookie: Cookie): Unit = {
    val httpHeader = new Header("Cookie", "CBSESSID=" + cookie.value)
    val httpHeaders = new java.util.ArrayList[Header]()
    httpHeaders.add(httpHeader)
    options.setProperty(HTTPConstants.HTTP_HEADERS, httpHeaders)
  }
}
