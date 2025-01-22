package com.mgs.message.data

data class Group(
    override val name: String = "GroupObject",
    override val id: Int = 0,
    override val icon: String = "Icon"
) : Target