package io.vending.machine.repository

import io.vending.machine.domain.User
import io.vending.machine.domain.UserRepository

class UserRepositoryImpl : UserRepository {
    private val users = arrayListOf<User>(User("mingyu"))

    override fun findUser(name: String): User? = users.firstOrNull { it.name == name }

    override fun addUsers(users: List<User>) {
        this.users.addAll(users)
    }
}
