package io.vending.machine.application

import io.vending.machine.domain.User
import io.vending.machine.domain.UserRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserService : KoinComponent {
    private val userRepository: UserRepository by inject()

    fun getDefaultUser(name: String) : User = userRepository.findUser(name) ?: error("Can't find default user")
}