package models.messages
import java.time.LocalDateTime

case class Invalidation(  requestDateAndTime: LocalDateTime = LocalDateTime.now(),
                          decisionDateAndTime: LocalDateTime = LocalDateTime.now(),
                          decision: String = "0",
                          initiatedByCustoms: String = "0",
                          justification: String
                        )
