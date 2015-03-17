package net.mtgto.garoon.user

import net.mtgto.garoon.Id
import org.sisioh.dddbase.core.model.Entity
import scala.xml.Node

class UserId(override val value: String) extends Id(value)

object UserId {
  def apply(value: String): UserId = new UserId(value)
}

trait User extends Entity[UserId] {
  /**
   * 表示されるユーザ名
   */
  val name: String

  /**
   * ログイン名
   */
  val loginName: String

  val organizationIds: Seq[OrganizationId]

  val primaryOrganizationId: Option[OrganizationId]
}

object User {
  private[this] case class DefaultUser(
    identity: UserId, name: String, loginName: String, organizationIds: Seq[OrganizationId],
    primaryOrganizationId: Option[OrganizationId]) extends User

  def apply(node: Node): User = {
    val identity = UserId((node \ "@key").text)
    val name = (node \ "@name").text
    val loginName = (node \ "@login_name").text
    val organizationIds = (node \ "organization").map(n => OrganizationId((n \ "@id").text))
    val primaryOrganizationId = (node \ "@primary_organization").headOption.map(id => OrganizationId(id.text))
    DefaultUser(identity, name, loginName, organizationIds, primaryOrganizationId)
  }
}
