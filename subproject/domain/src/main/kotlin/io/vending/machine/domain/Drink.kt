package io.vending.machine.domain

import java.time.OffsetDateTime
import java.util.UUID

data class Drink(
    override val id: Base.Id,
    val name: Name,
    val price: Price,
    val amount: Amount,
    override val created: Base.CreatedAt,
    override val modified: Base.ModifiedAt,
    override val deleted: Base.DeletedAt?,
) : Base {
    companion object {
        fun of(
            name: Name,
            price: Price,
            amount: Amount,
        ): Drink {
            val now = OffsetDateTime.now()

            return Drink(
                id = Base.Id(UUID.randomUUID()),
                name = name,
                price = price,
                amount = amount,
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
    value class Amount(
        val value: Int,
    ) {
        operator fun minus(quantity: Int): Amount = Amount(value - quantity)
    }

    val sufficientQuantity: Boolean
        get() = amount.value > 0

    fun buy() = this.copy(amount = this.amount - 1)

    fun checkAvailableBuy(coin: Int) = coin - this.price.value >= 0
}
