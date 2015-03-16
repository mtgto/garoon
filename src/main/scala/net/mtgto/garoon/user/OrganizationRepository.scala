package net.mtgto.garoon.user

import net.mtgto.garoon.{Authentication, GaroonClient}
import org.sisioh.dddbase.core.lifecycle.{EntityNotFoundException, EntityIOContext}
import org.sisioh.dddbase.core.lifecycle.sync.SyncEntityReader
import scala.util.Try
import scala.xml.XML

class OrganizationRepository(client: GaroonClient, auth: Authentication) extends SyncEntityReader[OrganizationId, Organization] {
  def resolve(identity: OrganizationId)(implicit ctx: EntityIOContext[Try]): Try[Organization] = {
    val actionName = "BaseGetOrganizationsById"
    val parameters = client.factory.createOMElement("parameters", null)
    val eventNode = client.factory.createOMElement("organization_id", null)
    eventNode.setText(identity.value)
    parameters.addChild(eventNode)

    val result = client.sendReceive(actionName, "/cbpapi/base/api", parameters)(auth, None)
    result.map { element =>
      val node = XML.loadString(element.toString)
      (node \ "returns" \ "organization").map(Organization(_)).headOption.getOrElse(throw new EntityNotFoundException)
    }
  }

  def containsByIdentity(identity: OrganizationId)(implicit ctx: EntityIOContext[Try]): Try[Boolean] =
    resolve(identity).map(_ => true)
}
