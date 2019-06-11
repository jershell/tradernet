package ru.tradernet.core


class AuthException(override val message: String) : ExpectedException(message)