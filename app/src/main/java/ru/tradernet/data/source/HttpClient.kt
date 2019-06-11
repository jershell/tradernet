package ru.tradernet.data.source

import io.ktor.client.HttpClient
import io.ktor.client.features.cookies.HttpCookies
import io.ktor.client.features.websocket.WebSockets

fun getHttpClient(): HttpClient {
    return HttpClient {
        expectSuccess = false
        install(HttpCookies)
    }
}

fun getWsClient(): HttpClient {
    return HttpClient {
        install(WebSockets)
        install(HttpCookies)
    }
}