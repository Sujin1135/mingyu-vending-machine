package io.vending.machine.domain

import java.time.OffsetDateTime
import java.util.UUID

interface Base {
    val id: Id
    val created: CreatedAt
    val modified: ModifiedAt
    val deleted: DeletedAt?

    @JvmInline
    value class Id(
        val value: UUID,
    )

    @JvmInline
    value class CreatedAt(
        val value: OffsetDateTime,
    )

    @JvmInline
    value class ModifiedAt(
        val value: OffsetDateTime,
    )

    @JvmInline
    value class DeletedAt(
        val value: OffsetDateTime,
    )
}
