package ru.tradernet.data.repository

import com.orhanobut.logger.Logger
import io.ktor.client.call.call
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import io.ktor.client.features.websocket.wss
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.response.readText
import io.ktor.http.*
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readBytes
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import kotlinx.coroutines.isActive
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.json.JSONArray
import org.json.JSONObject
import ru.tradernet.BuildConfig
import ru.tradernet.core.AuthException
import ru.tradernet.core.ExpectedException
import ru.tradernet.data.model.Quotation
import ru.tradernet.data.model.TopSecurities
import ru.tradernet.data.source.getHttpClient
import ru.tradernet.data.source.getWsClient
import java.net.URISyntaxException


@UseExperimental(ImplicitReflectionSerializer::class)
class StockMarketRepository(val appRepository: AppRepository) {
    private val httpClient = getHttpClient()
    lateinit var mSocket: Socket

    private val tickers: MutableList<String> = mutableListOf()
    private val stocks: MutableMap<String, Quotation> = mutableMapOf()

    fun unsubscribeOnUpdate() {
        mSocket.off("q")
        mSocket.off(Socket.EVENT_CONNECT)
        mSocket.off(Socket.EVENT_DISCONNECT)
        mSocket.off(Socket.EVENT_CONNECT_ERROR)
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT)
        if (mSocket.connected()) {
            mSocket.disconnect()
            Logger.d("WS disconnected")
        }
    }

    fun subscribeOnUpdate(block: (s: MutableMap<String, Quotation>) -> Unit) {
        try {
            mSocket = IO.socket("https://ws2.tradernet.ru?SID=${appRepository.auth!!.sid}")
        } catch (e: URISyntaxException) {
            Logger.d("socket err ${e.message}")
            throw ExpectedException("mSocket not connected ${e.message}")
        }

        mSocket.on(Socket.EVENT_DISCONNECT) { Logger.d("EVENT_DISCONNECT") }
        mSocket.on(Socket.EVENT_CONNECT_ERROR) { Logger.d("EVENT_CONNECT_ERROR") }
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT) { Logger.d("EVENT_CONNECT_TIMEOUT") }
        mSocket.on(Socket.EVENT_CONNECT) {
            val poisonArray = JSONArray()

            tickers.forEach {
                poisonArray.put(it)
            }

            Logger.d("socket connected")
            mSocket.emit("sup_updateSecurities2", poisonArray)
            Logger.d("subscribeOnUpdate sup_updateSecurities2")
        }
        mSocket.on("q") {
            it.forEach { msg ->
                if (msg is JSONObject) {
                    val sMsg = msg.getJSONArray("q").toString()
                    try {
                        val nextUpdate = Json.nonstrict.parse(Quotation.serializer().list, sMsg)
                        nextUpdate.forEach { q ->
                            if (stocks.containsKey(q.code)) {

                                if (q.changeInThePriceOfTheLastTradeInPoints != null) {
                                    stocks[q.code]?.changeInThePriceOfTheLastTradeInPoints =
                                        q.changeInThePriceOfTheLastTradeInPoints
                                }

                                if (!q.exchangeOfTheLatestTrade.isNullOrEmpty()) {
                                    stocks[q.code]?.exchangeOfTheLatestTrade = q.exchangeOfTheLatestTrade
                                }

                                if (q.percentageChangeRelative != null) {
                                    stocks[q.code]?.percentageChangeRelative = q.percentageChangeRelative
                                }

                                if (q.lastTradePrice is Float) {
                                    stocks[q.code]?.prevLastTradePrice = stocks[q.code]?.lastTradePrice!!
                                    stocks[q.code]?.lastTradePrice = q.lastTradePrice
                                }

                                if (!q.timeOfLastTrade.isNullOrEmpty()) {
                                    stocks[q.code]?.timeOfLastTrade = q.timeOfLastTrade
                                }

                            } else {
                                stocks[q.code] = q
                            }
                        }
                        block(stocks)
                    } catch (e: MissingFieldException) {
                        Logger.w(e.message.toString())
                    }
                }
            }
        }
        mSocket.connect()
    }

    suspend fun getTopSecurities() {
        val p = JsonObject(
            mapOf(
                "cmd" to JsonPrimitive("getTopSecurities"),
                "params" to JsonObject(
                    mapOf(
                        "type" to JsonPrimitive("stocks"),
                        "exchange" to JsonPrimitive("russia"),
                        "gainers" to JsonPrimitive("0"),
                        "limit" to JsonPrimitive("10")
                    )
                )
            )
        )

        val response = httpClient.call {
            method = HttpMethod.Get
            url.takeFrom(BuildConfig.API_URL)
            header(HttpHeaders.Cookie, renderSetCookieHeader(appRepository.authCookie))
            parameter("q", Json.stringify(p))
        }.response

        val payload = response.readText()

        try {
            val element = Json.nonstrict.parseJson(payload)

            if (element.jsonObject.containsKey("error")) {
                val reason: String = element.jsonObject["error"].toString()
                Logger.w(reason)
                throw AuthException(reason)
            } else {
                tickers.clear()
                tickers.addAll(0, Json.nonstrict.fromJson<TopSecurities>(element).tickers)
                Logger.d("tickers $tickers")
            }

        } catch (e: NoSuchElementException) {
            Logger.w("Parser error ${e.message}")
            throw ExpectedException(e.message.toString())
        } catch (e: SerializationException) {
            Logger.w("Parser error ${e.message}")
            throw ExpectedException(e.message.toString())
        }
    }
}