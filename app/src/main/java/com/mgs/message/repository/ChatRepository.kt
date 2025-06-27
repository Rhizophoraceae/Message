package com.mgs.message.repository

class ChatRepository(
    private val chatLocalDataSource: ChatLocalDataSource,
    private val chatRemoteDataSource: ChatRemoteDataSource
) {
}

class ChatRemoteDataSource {}

class ChatLocalDataSource {}