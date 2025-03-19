package io.vending.machine

import io.vending.machine.config.appModule
import org.koin.core.context.GlobalContext.startKoin

fun main() {
    startKoin {
        modules(appModule)
    }
    println("Hello World!")
}