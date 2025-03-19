package io.vending.machine.config

import io.vending.machine.application.UserService
import io.vending.machine.domain.UserRepository
import io.vending.machine.repository.UserRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<UserService> { UserService(get()) }
}