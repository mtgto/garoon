package net.mtgto.garoon.schedule

import net.mtgto.garoon.Id

class Member(id: Id, order: Option[Id])

case class User(id: Id, name: String, order: Option[Id]) extends Member(id, order)

case class Facility(id: Id, name: String, order: Option[Id]) extends Member(id, order)