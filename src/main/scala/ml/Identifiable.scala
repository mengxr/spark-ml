package ml

import java.util.UUID

/**
 * Something with a unique id.
 */
trait Identifiable {
  val id: String
}

object Identifiable {
  private[ml] def randomId(): String = UUID.randomUUID().toString.take(8)
}
