package ru.tradernet.data.repository

import ru.tradernet.data.model.*
import ru.tradernet.data.source.getHttpClient
import com.orhanobut.logger.Logger
import io.ktor.client.call.call
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.response.readText
import io.ktor.http.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.stringify
import ru.tradernet.BuildConfig.API_URL
import ru.tradernet.core.AuthException
import ru.tradernet.core.ExpectedException
import ru.tradernet.data.AUTH_STATUS


@UseExperimental(ImplicitReflectionSerializer::class)
class AppRepository(
    private val persistentRepository: PersistentRepository
) {
    private val httpClient = getHttpClient()

    var auth: Auth? = null

    val authCookie: Cookie
        get() = Cookie("SID", auth!!.sid, path = "/", domain = ".tradernet.ru")

    suspend fun resumeSession(): AUTH_STATUS {
        val sid = persistentRepository.read("SID", "")
        val userId = persistentRepository.read("USER_ID", "")

        return if (sid.isNotEmpty() && userId.isNotEmpty()) {
            auth = Auth(false, sid, userId)
            getOPQ()
            AUTH_STATUS.AUTHENTICATED
        } else {
            AUTH_STATUS.NOT_AUTHENTICATED
        }
    }

    suspend fun getOPQ() {
        val response = httpClient.call {
            method = HttpMethod.Get
            url.takeFrom(API_URL)
            header(HttpHeaders.Cookie, renderSetCookieHeader(authCookie))
            parameter(
                "q", Json.stringify(
                    mapOf(
                        "cmd" to "getOPQ",
                        "params" to ""
                    )
                )
            )
        }.response

        val payload = response.readText()

        try {
            val element = Json.nonstrict.parseJson(payload)
            val objElement = element.jsonObject
            if (objElement.containsKey("error")) {
                val reason: String = element.jsonObject["error"].toString()
                Logger.w(reason)
                throw AuthException(reason)
            }
        } catch (e: NoSuchElementException) {
            Logger.w("Parser error ${e.message}")
            throw ExpectedException(e.message.toString())
        } catch (e: SerializationException) {
            Logger.w("Parser error ${e.message}")
            throw ExpectedException(e.message.toString())
        }
        Logger.d(payload)
    }

    suspend fun login(login: String, password: String): AUTH_STATUS {

        val params = ParametersBuilder()
            .apply {
                append("login", login)
                append("password", password)
                append("rememberMe", "1")
            }

        val res = httpClient.call {
            method = HttpMethod.Post
            url.takeFrom("$API_URL/check-login-password ")
            body = FormDataContent(formData = params.build())
        }.response

        val payload = res.readText()

        Logger.d("status ${res.status.value} | $payload")

        try {
            val element = Json.nonstrict.parseJson(payload)
            if (element.jsonObject.containsKey("error")) {
                val reason: String = element.jsonObject["error"].toString()
                Logger.w(reason)
                throw AuthException(reason)
            } else {
                auth = Json.nonstrict.fromJson(element)
                getOPQ()
                persistentRepository.save("USER_ID", auth!!.userId)
                persistentRepository.save("SID", auth!!.sid)
                return AUTH_STATUS.AUTHENTICATED
            }
        } catch (e: NoSuchElementException) {
            Logger.w("Parser error ${e.message}")
            throw ExpectedException(e.message.toString())
        } catch (e: SerializationException) {
            Logger.w("Parser error ${e.message}")
            throw ExpectedException(e.message.toString())
        }
    }

    fun signOut() {
        auth = null
        persistentRepository.remove("SID")
        persistentRepository.remove("USER_ID")
    }
}