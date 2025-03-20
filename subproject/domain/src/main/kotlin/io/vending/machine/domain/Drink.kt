package io.vending.machine.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Drink(
    override val id: Base.Id,
    val name: Name,
    val price: Price,
    val quantity: Quantity,
    override val created: Base.CreatedAt,
    override val modified: Base.ModifiedAt,
    override val deleted: Base.DeletedAt?,
) : Base {
    companion object {
        fun of(
            name: Name,
            price: Price,
            quantity: Quantity,
        ): Drink {
            val now = OffsetDateTime.now()

            return Drink(
                id = Base.Id(UUID.randomUUID()),
                name = name,
                price = price,
                quantity = quantity,
                created = Base.CreatedAt(value = now),
                modified = Base.ModifiedAt(value = now),
                deleted = null,
            )
        }
    }

    @JvmInline
    value class Name(
        val value: String,
    )

    @JvmInline
    value class Price(
        val value: Int,
    )

    @JvmInline
    value class Quantity(
        val value: Int,
    ) {
        operator fun minus(quantity: Int): Quantity = Quantity(value - quantity)
    }

    val sufficientQuantity: Boolean
        get() = quantity.value > 0

    fun buy() = this.copy(quantity = this.quantity - 1)

    fun checkAvailableBuy(coin: Int) = coin - this.price.value >= 0
}
