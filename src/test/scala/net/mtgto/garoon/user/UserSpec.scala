package net.mtgto.garoon.user

import org.specs2.mutable.Specification

class UserSpec extends Specification {
  "User" should {
    "be able to create an event from valid xml" in {
      val xml =
        <user
          key="48"
          version="1245376338"
          order="0"
          login_name="naka"
          name="那珂"
          status="0" />
      val user = User(xml)
      user.identity.value must_== "48"
      user.loginName must_== "naka"
      user.name must_== "那珂"
    }
  }
}
