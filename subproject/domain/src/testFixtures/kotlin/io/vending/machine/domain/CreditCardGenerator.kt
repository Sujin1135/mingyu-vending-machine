package io.vending.machine.domain

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import net.jqwik.api.Arbitraries

object CreditCardGenerator {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .build()

    fun generate() =
        fixtureMonkey
            .giveMeBuilder<CreditCard>()
            .set(
                "companyName",
                CreditCard.CompanyName(
                    Arbitraries
                        .strings()
                        .withCharRange('a', 'z')
                        .ofMinLength(2)
                        .ofMaxLength(10)
                        .sample(),
                ),
            ).set(
                "cardNumberPart1",
                CreditCard.CardNumberPart1(getRandomCardNumberPart()),
            ).set(
                "cardNumberPart2",
                CreditCard.CardNumberPart2(getRandomCardNumberPart()),
            ).set(
                "cardNumberPart3",
                CreditCard.CardNumberPart3(getRandomCardNumberPart()),
            ).set(
                "cardNumberPart4",
                CreditCard.CardNumberPart4(getRandomCardNumberPart()),
            ).build()
            .sample()

    private fun getRandomCardNumberPart() = Arbitraries.integers().between(1000, 9999).sample()
}
