package net.mtgto.garoon

import com.github.nscala_time.time.Imports._
import java.net.URI
import org.apache.axis2.Constants
import org.apache.axis2.addressing.EndpointReference
import org.apache.axis2.client.{Options, ServiceClient}
import org.apache.axiom.om.{OMAbstractFactory, OMElement}
import org.apache.axiom.soap.SOAP12Constants
import org.apache.axis2.transport.http.HTTPConstants
import scala.util.Try

class GaroonClient(username: String, password: String, uri: URI) {
  private[this] val serviceClient = new ServiceClient

  private[this] val scheme = uri.getScheme

  val factory = OMAbstractFactory.getOMFactory

  private[this] val namespace = factory.createOMNamespace("http://wsdl.cybozu.co.jp/api/2008", "tns")

  def sendReceive(actionName: String, actionPath: String, parameters: OMElement): Try[OMElement] = {
    serviceClient.removeHeaders

    val header = createHeader(actionName, DateTime.now, DateTime.now + 1.day)
    serviceClient.addHeader(header)

    val option = createOption(actionName, actionPath)
    serviceClient.setOptions(option)

    val request = factory.createOMElement(actionName, namespace)
    request.addChild(parameters)

    println(header)
    println(request.getText)
    println(request)

    Try(serviceClient.sendReceive(request))
    //throw new RuntimeException
  }

  private[this] def createHeader(actionName: String, createdTime: DateTime, expiredTime: DateTime): OMElement = {
    val namespace = factory.createOMNamespace(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI, "soapenv")
    val header = factory.createOMElement("Header", namespace)

    header.addChild(createAction(actionName))
    header.addChild(createSecurity)
    header.addChild(createTimestamp(createdTime, expiredTime))

    header
  }

  private[this] def createAction(actionName: String): OMElement = {
    val namespace = factory.createOMNamespace("http://schemas.xmlsoap.org/ws/2003/03/addressing", "")
    val actionElement = factory.createOMElement("Action", namespace)
    actionElement.addChild(factory.createOMText(actionElement, actionName))
    actionElement
  }

  private[this] def createSecurity: OMElement = {
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

  private[this] def createOption(actionName: String, actionPath: String): Options = {
    val options = new Options
    val actionUri = uri.toString + actionPath
    options.setTo(new EndpointReference(actionUri.toString))
    options.setTransportInProtocol(scheme)
    options.setProperty(HTTPConstants.CHUNKED, Constants.VALUE_FALSE)
    options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI)
    options.setAction(actionName)
    options
  }
}
