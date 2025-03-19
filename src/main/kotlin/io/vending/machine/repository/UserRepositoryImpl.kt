package io.vending.machine.repository

import io.vending.machine.domain.User
import io.vending.machine.domain.UserRepository

class UserRepositoryImpl : UserRepository {
    private val _users = arrayListOf<User>()

    override fun findUser(name: String): User? {
        return _users.firstOrNull { it.name == name }
    }

    override fun addUsers(users : List<User>) {
        _users.addAll(users)
    }
}
