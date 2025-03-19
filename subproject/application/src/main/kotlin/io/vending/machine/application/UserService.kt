package io.vending.machine.application

import io.vending.machine.domain.User
import io.vending.machine.domain.UserRepository

class UserService(
    private val userRepository: UserRepository,
) {
    fun getDefaultUser(name: String): User = userRepository.findUser(name) ?: error("Can't find default user")
}
