package io.vending.machine

import io.vending.machine.application.UserService
import io.vending.machine.config.appModule
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.get

fun main() {
    startKoin {
        modules(appModule)
    }

    val service = get<UserService>(UserService::class.java)
    val user = service.getDefaultUser("mingyu")
    println("Hello ${user.name}!")
}
