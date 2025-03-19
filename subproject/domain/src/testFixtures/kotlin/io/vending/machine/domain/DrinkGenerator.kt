package io.vending.machine.domain

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import net.jqwik.api.Arbitraries

object DrinkGenerator {
    private val fixtureMonkey =
        FixtureMonkey
            .builder()
            .plugin(KotlinPlugin())
            .build()

    fun generate() =
        fixtureMonkey
            .giveMeBuilder<Drink>()
            .set(
                "name",
                Drink.Name(
                    Arbitraries
                        .strings()
                        .withCharRange('a', 'z')
                        .ofMinLength(2)
                        .ofMaxLength(10)
                        .sample(),
                ),
            ).set(
                "price",
                Drink.Price(
                    Arbitraries.integers().between(500, 3000).sample(),
                ),
            ).set(
                "amount",
                Drink.Amount(
                    Arbitraries.integers().between(1, 50).sample(),
                ),
            ).build()
            .sample()
}
