package io.vending.machine.domain

data class CreditCard(
    val companyName: CompanyName,
    val cardNumberPart1: CardNumberPart1,
    val cardNumberPart2: CardNumberPart2,
    val cardNumberPart3: CardNumberPart3,
    val cardNumberPart4: CardNumberPart4,
) {
    @JvmInline
    value class CompanyName(
        val value: String,
    )

    @JvmInline
    value class CardNumberPart1(
        val value: Int,
    )

    @JvmInline
    value class CardNumberPart2(
        val value: Int,
    )

    @JvmInline
    value class CardNumberPart3(
        val value: Int,
    )

    @JvmInline
    value class CardNumberPart4(
        val value: Int,
    )
}
