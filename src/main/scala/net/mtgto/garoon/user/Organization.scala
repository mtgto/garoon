package net.mtgto.garoon.user

import net.mtgto.garoon.Id
import org.sisioh.dddbase.core.model.Entity
import scala.xml.Node

class OrganizationId(override val value: String) extends Id(value)

object OrganizationId {
  def apply(value: String): OrganizationId = new OrganizationId(value)
}

trait Organization extends Entity[OrganizationId] {
  val name: String

  val description: String

  val parentOrganizationId: OrganizationId

  val members: Seq[UserId]
}

object Organization {
  private[this] case class DefaultOrganization(
    identity: OrganizationId, name: String, description: String,
    parentOrganizationId: OrganizationId, members: Seq[UserId])
    extends Organization

  def apply(node: Node): Organization = {
    val identity = OrganizationId((node \ "@key").text)
    val name = (node \ "@name").text
    val description = (node \ "@description").text
    val parentOrganizationId = OrganizationId((node \ "@parent_organization").text)
    val members = (node \ "members" \ "user").map(n => UserId((n \ "@id").text))
    DefaultOrganization(identity, name, description, parentOrganizationId, members)
  }
}
