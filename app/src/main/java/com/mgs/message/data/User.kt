package com.mgs.message.data

data class User(
    override val name: String = "UserObject",
    override val id: Int = 0,
    override val icon: String = "Icon"
) : Target
