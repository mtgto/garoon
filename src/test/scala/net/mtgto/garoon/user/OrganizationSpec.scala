package net.mtgto.garoon.user

import org.specs2.mutable.Specification

class OrganizationSpec extends Specification {
  "Organization" should {
    "be able to create an organization from valid xml" in {
      val xml =
          <organization
          key="1032"
          version="1426505595"
          order="3"
          name="鎮守府海域"
          parent_organization="1029" />
      val org = Organization(xml)
      org.identity.value must_== "1032"
      org.name must_== "鎮守府海域"
    }
  }
}
