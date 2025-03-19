package io.vending.machine.domain

interface UserRepository {
    fun findUser(name : String): User?
    fun addUsers(users : List<User>)
}