package ml

import java.util.UUID

/**
 * Something with a unique id.
 */
trait Identifiable {
  val id: String
}

object Identifiable {
  def randomId(): String = UUID.randomUUID().toString
}
